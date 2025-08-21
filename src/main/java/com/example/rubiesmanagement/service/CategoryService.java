package com.example.rubiesmanagement.service;

import com.example.rubiesmanagement.dto.response.CategoryResponse;
import com.example.rubiesmanagement.form.product.CategoryForm;
import jakarta.validation.Valid;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(@Valid CategoryForm form);

    CategoryResponse updateCategory(Integer id, @Valid CategoryForm form);

    void deleteCategory(Integer id);

    CategoryResponse getCategoryById(Integer id);

    List<CategoryResponse> getAllCategory();
}
