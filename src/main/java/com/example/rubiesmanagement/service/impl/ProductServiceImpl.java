package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.dto.response.*;
import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.exception.NotFoundException;
import com.example.rubiesmanagement.form.product.FilterProductForm;
import com.example.rubiesmanagement.form.product.ProductForm;
import com.example.rubiesmanagement.form.product.ProductImageForm;
import com.example.rubiesmanagement.form.product.ProductVariantForm;
import com.example.rubiesmanagement.mapper.ProductMapper;
import com.example.rubiesmanagement.model.*;
import com.example.rubiesmanagement.repository.CategoryRepository;
import com.example.rubiesmanagement.repository.ColorRepository;
import com.example.rubiesmanagement.repository.ProductRepository;
import com.example.rubiesmanagement.repository.specification.ProductSpecification;
import com.example.rubiesmanagement.service.FileStorageService;
import com.example.rubiesmanagement.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ColorRepository colorRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductForm form) {
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new BusinessException("Không tìm thấy category", ErrorCodeConstant.CATEGORY_NOT_FOUND_BY_ID));

        Product product = new Product();
        product.setName(form.getName());
        product.setSku(form.getSku());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setDiscountPrice(form.getDiscountPrice());
        product.setCategory(category);
        product.setInStock(form.getInStock());
        // Xử lý main image (nếu có)
        if (form.getMainImageUrl() != null && !form.getMainImageUrl().isEmpty()) {
            String mainImageUrl = fileStorageService.storeFile(form.getMainImageUrl(), "products");
            product.setMainImageUrl(mainImageUrl);
        }

        // Kiểm tra SKU
        if (productRepository.existsBySku(form.getSku())) {
            throw new BusinessException(ErrorCodeConstant.DUPLICATE_SKU, "SKU đã tồn tại: " + form.getSku());
        }

        // Lưu trước product (để có id)
        productRepository.save(product);

        // Xử lý variants
        if (form.getVariant() != null) {
            for (ProductVariantForm variantForm : form.getVariant()) {
                ProductVariant variant = new ProductVariant();
                variant.setColor(colorRepository.findById(variantForm.getColorId())
                        .orElseThrow(() -> new BusinessException("Không tìm thấy màu", ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID)));
                variant.setSize(variantForm.getSize());
                variant.setQuantity(variantForm.getQuantity());
                variant.setProduct(product);

                // Lưu variant trước -> để có id
                product.getVariants().add(variant);

                if (variantForm.getImages() != null) {
                    boolean hasMain = false; // kiểm tra đã có ảnh nào là main chưa

                    for (ProductImageForm imgForm : variantForm.getImages()) {
                        String imageUrl = fileStorageService.storeFile(imgForm.getImageUrl(), "products");

                        ProductImage img = new ProductImage();
                        img.setImageUrl(imageUrl);
                        img.setIsMain(imgForm.getIsMain() != null ? imgForm.getIsMain() : false);
                        img.setProductVariant(variant);

                        // nếu là ảnh main -> set cho variant
                        if (img.getIsMain()) {
                            hasMain = true;
                            variant.setVariantMainImageUrl(imageUrl);
                            // nếu product chưa có main image thì gán luôn
                            if (product.getMainImageUrl() == null) {
                                product.setMainImageUrl(imageUrl);
                            }
                        }

                        variant.getImages().add(img);
                    }

                    // Nếu user không chọn ảnh nào là main -> lấy ảnh đầu tiên làm main
                    if (!hasMain && !variant.getImages().isEmpty()) {
                        ProductImage firstImg = variant.getImages().get(0);
                        firstImg.setIsMain(true); // gán main cho ảnh đầu tiên
                        variant.setVariantMainImageUrl(firstImg.getImageUrl());

                        if (product.getMainImageUrl() == null) {
                            product.setMainImageUrl(firstImg.getImageUrl());
                        }
                    }
                }
            }
        }

        // Save lại để cascade images
        Product saved = productRepository.save(product);

        return modelMapper.map(saved, ProductResponse.class);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Integer id, ProductForm form) {
        // Tìm product hiện tại
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeConstant.PRODUCT_NOT_FOUND_BY_ID,
                        "Không tìm thấy sản phẩm có ID: " + id));

        // --- Kiểm tra SKU duplicate (nếu thay đổi SKU) ---
        if (!product.getSku().equals(form.getSku())) {
            if (productRepository.existsBySku(form.getSku())) {
                throw new BusinessException(ErrorCodeConstant.DUPLICATE_SKU,
                        "SKU đã tồn tại: " + form.getSku());
            }
        }

        // --- Cập nhật thông tin cơ bản ---
        product.setName(form.getName());
        product.setSku(form.getSku());
        product.setDescription(form.getDescription());
        product.setPrice(form.getPrice());
        product.setDiscountPrice(form.getDiscountPrice());
        product.setInStock(form.getInStock());

        // --- Cập nhật category ---
        Category category = categoryRepository.findById(form.getCategoryId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeConstant.CATEGORY_NOT_FOUND_BY_ID,
                        "Không tìm thấy danh mục với ID: " + form.getCategoryId()));
        product.setCategory(category);

        // --- Cập nhật main image (chỉ khi có ảnh mới upload) ---
        if (form.getMainImageUrl() != null && !form.getMainImageUrl().isEmpty()) {
            String savedPath = fileStorageService.storeFile(form.getMainImageUrl(), "products");
            product.setMainImageUrl(savedPath);
        }

        // --- Cập nhật variants ---
        if (form.getVariant() != null) {
            updateProductVariants(product, form.getVariant());
        } else {
            // Nếu không có variant nào trong form, xóa hết variants cũ
            product.getVariants().clear();
        }

        // Lưu product
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    private void updateProductVariants(Product product, List<ProductVariantForm> variantForms) {
        List<ProductVariant> currentVariants = product.getVariants();

        // Tạo map để dễ lookup variants hiện tại
        Map<Integer, ProductVariant> currentVariantMap = currentVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        // Danh sách variant IDs từ form (để biết variant nào cần giữ lại)
        List<Integer> variantIdsInForm = variantForms.stream()
                .map(ProductVariantForm::getId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        // Xóa các variants không còn trong form
        currentVariants.removeIf(variant ->
                !variantIdsInForm.contains(variant.getId()));

        // Xử lý từng variant trong form
        for (ProductVariantForm variantForm : variantForms) {
            if (variantForm.getId() != null && currentVariantMap.containsKey(variantForm.getId())) {
                // Cập nhật variant hiện có
                updateExistingVariant(currentVariantMap.get(variantForm.getId()), variantForm);
            } else {
                // Tạo variant mới
                ProductVariant newVariant = createNewVariant(product, variantForm);
                currentVariants.add(newVariant);
            }
        }
    }

    private void updateExistingVariant(ProductVariant variant, ProductVariantForm variantForm) {
        // Cập nhật thông tin cơ bản của variant
        Color color = colorRepository.findById(variantForm.getColorId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID,
                        "Không tìm thấy màu sắc với ID: " + variantForm.getColorId()));

        variant.setColor(color);
        variant.setSize(variantForm.getSize());
        variant.setQuantity(variantForm.getQuantity());

        // Cập nhật images của variant
        if (variantForm.getImages() != null) {
            updateVariantImages(variant, variantForm.getImages());
        }
    }

    private ProductVariant createNewVariant(Product product, ProductVariantForm variantForm) {
        Color color = colorRepository.findById(variantForm.getColorId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeConstant.COLOR_NOT_FOUND_BY_ID,
                        "Không tìm thấy màu sắc với ID: " + variantForm.getColorId()));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setColor(color);
        variant.setSize(variantForm.getSize());
        variant.setQuantity(variantForm.getQuantity());

        // Thêm images cho variant mới
        if (variantForm.getImages() != null) {
            List<ProductImage> images = new ArrayList<>();
            boolean hasMainImage = false;

            for (ProductImageForm imageForm : variantForm.getImages()) {
                if (imageForm.getImageUrl() != null && !imageForm.getImageUrl().isEmpty()) {
                    String savedPath = fileStorageService.storeFile(imageForm.getImageUrl(), "products");

                    ProductImage image = new ProductImage();
                    image.setImageUrl(savedPath);
                    image.setIsMain(imageForm.getIsMain() != null ? imageForm.getIsMain() : false);
                    image.setProductVariant(variant);

                    images.add(image);

                    // Set variant main image
                    if (image.getIsMain()) {
                        hasMainImage = true;
                        variant.setVariantMainImageUrl(savedPath);
                    }
                }
            }

            // Nếu không có ảnh nào được đánh dấu main, lấy ảnh đầu tiên
            if (!hasMainImage && !images.isEmpty()) {
                images.get(0).setIsMain(true);
                variant.setVariantMainImageUrl(images.get(0).getImageUrl());
            }

            variant.setImages(images);
        }

        return variant;
    }

    private void updateVariantImages(ProductVariant variant, List<ProductImageForm> imageForms) {
        List<ProductImage> currentImages = variant.getImages();

        // ✅ Nếu FE không gửi gì => giữ nguyên ảnh cũ
        if (imageForms == null) {
            return;
        }

        // Nếu FE gửi danh sách rỗng => nghĩa là muốn xóa hết ảnh
        if (imageForms.isEmpty()) {
            currentImages.clear();
            variant.setVariantMainImageUrl(null);
            return;
        }

        // Map để lookup ảnh hiện tại
        Map<Integer, ProductImage> currentImageMap = currentImages.stream()
                .collect(Collectors.toMap(ProductImage::getId, img -> img));

        // Lấy danh sách id ảnh từ FE
        List<Integer> imageIdsInForm = imageForms.stream()
                .map(ProductImageForm::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Xóa ảnh nào không còn trong form
        currentImages.removeIf(image -> image.getId() != null && !imageIdsInForm.contains(image.getId()));

        boolean hasMainImage = false;

        // Xử lý ảnh từ FE
        for (ProductImageForm imageForm : imageForms) {
            if (imageForm.getId() != null && currentImageMap.containsKey(imageForm.getId())) {
                // Ảnh cũ: chỉ update isMain
                ProductImage existingImage = currentImageMap.get(imageForm.getId());
                existingImage.setIsMain(imageForm.getIsMain() != null ? imageForm.getIsMain() : false);

                if (existingImage.getIsMain()) {
                    hasMainImage = true;
                    variant.setVariantMainImageUrl(existingImage.getImageUrl());
                }

            } else if (imageForm.getImageUrl() != null && !imageForm.getImageUrl().isEmpty()) {
                // Ảnh mới: upload file
                String savedPath = fileStorageService.storeFile(imageForm.getImageUrl(), "products");

                ProductImage newImage = new ProductImage();
                newImage.setImageUrl(savedPath);
                newImage.setIsMain(imageForm.getIsMain() != null ? imageForm.getIsMain() : false);
                newImage.setProductVariant(variant);

                currentImages.add(newImage);

                if (newImage.getIsMain()) {
                    hasMainImage = true;
                    variant.setVariantMainImageUrl(savedPath);
                }
            }
        }

        // Nếu không có ảnh main, chọn ảnh đầu tiên
        if (!hasMainImage && !currentImages.isEmpty()) {
            ProductImage firstImage = currentImages.get(0);
            firstImage.setIsMain(true);
            variant.setVariantMainImageUrl(firstImage.getImageUrl());
        }
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeConstant.PRODUCT_NOT_FOUND_BY_ID,
                        "Không tìm thấy sản phẩm với ID: " + id
                ));

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setDiscountPrice(product.getDiscountPrice());
        response.setInStock(product.getInStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());


        // Map Category
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setId(product.getCategory().getId());
        categoryResponse.setName(product.getCategory().getName());
        categoryResponse.setImage(product.getCategory().getImage());
        response.setCategory(categoryResponse);

        // Map Variants
        List<ProductVariantResponse> variants = product.getVariants().stream()
                .map(variant -> {
                    ProductVariantResponse vr = new ProductVariantResponse();
                    vr.setId(variant.getId());
                    vr.setColorName(variant.getColor().getName());
                    vr.setSize(variant.getSize());
                    vr.setQuantity(variant.getQuantity());

                    // Map images của variant
                    List<ProductImageResponse> imageResponses = variant.getImages().stream()
                            .map(img -> {
                                ProductImageResponse ir = new ProductImageResponse();
                                ir.setId(img.getId());
                                ir.setImageUrl(img.getImageUrl());
                                ir.setIsMain(img.getIsMain()); // lấy isMain từ entity
                                return ir;
                            })
                            .collect(Collectors.toList());

                    vr.setImages(imageResponses);
                    return vr;
                })
                .collect(Collectors.toList());

        response.setVariants(variants);

        return response;
    }

    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException(ErrorCodeConstant.PRODUCT_NOT_FOUND_BY_ID, "Không tìm thấy sản phẩm với ID: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<FilterProductResponse> filterProducts(FilterProductForm filter) {
        Specification<Product> specification = ProductSpecification.alwaysTrue()
                .and(ProductSpecification.hasName(filter.getName()))
                .and(ProductSpecification.hasCategory(filter.getCategoryId()))
                .and(ProductSpecification.hasDiscount(filter.getHasDiscount()))
                .and(ProductSpecification.hasSku(filter.getSku()))
                .and(ProductSpecification.priceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(ProductSpecification.isInStock(filter.getInStock()));
        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Product> page = productRepository.findAll(specification, pageable);
        return page.map(product -> modelMapper.map(product, FilterProductResponse.class));
    }

    @Override
    public List<ProductResponse> getProductsByCategory(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);

        return products.stream()
                .map(ProductMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getTop4NewestProducts() {
        List<Product> products = productRepository.findTop4ByOrderByCreatedAtDesc();
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }
}