package com.example.rubiesmanagement.controller;

import com.example.rubiesmanagement.dto.ApiResponse;
import com.example.rubiesmanagement.dto.response.CategoryResponse;
import com.example.rubiesmanagement.form.product.CategoryForm;
import com.example.rubiesmanagement.service.CategoryService;
import com.example.rubiesmanagement.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @PostMapping()
    public ResponseEntity<ApiResponse> createCategory(
            @Valid @ModelAttribute CategoryForm form) {
        CategoryResponse created = categoryService.createCategory(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Tạo danh mục thành công", created));
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute CategoryForm form) {
        CategoryResponse response = categoryService.updateCategory(id, form);
        return ResponseEntity.ok(new ApiResponse("Cập nhật danh mục thành công", response));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(new ApiResponse("Xóa danh mục thành công", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Integer id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(new ApiResponse("Truy xuất danh mục thành công", response));
    }

    @GetMapping()
    public ResponseEntity<ApiResponse> getAllCategory() {
        List<CategoryResponse> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(new ApiResponse("Đã lấy tất cả danh mục", categories));
    }
}
