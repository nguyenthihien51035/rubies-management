package com.example.rubiesmanagement.controller;

import com.example.rubiesmanagement.dto.ApiResponse;
import com.example.rubiesmanagement.dto.response.FilterProductResponse;
import com.example.rubiesmanagement.dto.response.ProductResponse;
import com.example.rubiesmanagement.form.product.FilterProductForm;
import com.example.rubiesmanagement.form.product.ProductForm;
import com.example.rubiesmanagement.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createProduct(@Valid @ModelAttribute ProductForm form) {
        ProductResponse productRes = productService.createProduct(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo sản phẩm thành công", productRes));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateProduct(@PathVariable Integer id, @Valid @ModelAttribute ProductForm form) {
        ProductResponse productResponse = productService.updateProduct(id, form);
        return ResponseEntity.ok(new ApiResponse("Cập nhật sản phẩm thành công", productResponse));
    }

    //Lấy sản phẩm theo id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Integer id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(new ApiResponse("Chi tiết sản phẩm", product));
    }


    //Xóa sản phẩm
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(new ApiResponse("Xóa sản phẩm thành công", null));
    }

    @GetMapping("filter")
    public ResponseEntity<ApiResponse> filterProducts(@ModelAttribute FilterProductForm filter) {
        Page<FilterProductResponse> page = productService.filterProducts(filter);
        return ResponseEntity.ok(new ApiResponse("Lọc sản phẩm thành công", page));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable Integer categoryId) {
        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(new ApiResponse("Lấy sản phẩm theo category thành công", products));
    }

    @GetMapping("/top4-newest")
    public ResponseEntity<ApiResponse> getTop4NewestProducts() {
        List<ProductResponse> products = productService.getTop4NewestProducts();
        return ResponseEntity.ok(new ApiResponse("Lấy top 4 sản phẩm mới nhất thành công", products));
    }

}
