package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.dto.response.CategoryResponse;
import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.exception.NotFoundException;
import com.example.rubiesmanagement.form.product.CategoryForm;
import com.example.rubiesmanagement.model.Category;
import com.example.rubiesmanagement.repository.CategoryRepository;
import com.example.rubiesmanagement.service.CategoryService;
import com.example.rubiesmanagement.service.FileStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    public final CategoryRepository categoryRepository;
    public final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryForm form) {
        if (categoryRepository.existsByName(form.getName())) {
            throw new BusinessException("Tên danh mục đã tồn tại", ErrorCodeConstant.CATEGORY_NAME_ALREADY_EXISTS);
        }

        Category category = modelMapper.map(form, Category.class);

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            if (!fileStorageService.isValidImage(form.getImage())) {
                throw new BusinessException("Định dạng ảnh không hợp lệ", ErrorCodeConstant.INVALID_FILE_TYPE);
            }
            String imageUrl = fileStorageService.storeFile(form.getImage(), "categories");
            category.setImage(imageUrl);
        }
        Category saved = categoryRepository.save(category);
        return modelMapper.map(saved, CategoryResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Integer id, CategoryForm form) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy danh mục", ErrorCodeConstant.CATEGORY_NOT_FOUND_BY_ID));

        // Kiểm tra trùng tên (ngoại trừ chính nó)
        if (categoryRepository.existsByName(form.getName())
                && !category.getId().equals(id)) {
            throw new BusinessException("Tên danh mục đã tồn tại", ErrorCodeConstant.CATEGORY_NAME_ALREADY_EXISTS);
        }

        category.setName(form.getName());

        if (form.getImage() != null && !form.getImage().isEmpty()) {
            if (!fileStorageService.isValidImage(form.getImage())) {
                throw new BusinessException("Định dạng ảnh không hợp lệ", ErrorCodeConstant.INVALID_FILE_TYPE);
            }

            String imageUrl = fileStorageService.storeFile(form.getImage(), "categories");
            category.setImage(imageUrl);
        }

        Category updated = categoryRepository.save(category);

        return modelMapper.map(updated, CategoryResponse.class);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục trong hệ thống", ErrorCodeConstant.CATEGORY_NOT_FOUND_BY_ID));
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy danh mục trong hệ thống", ErrorCodeConstant.CATEGORY_NOT_FOUND_BY_ID));
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAllCategory() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> modelMapper.map(c, CategoryResponse.class))
                .collect(Collectors.toList());
    }
}
