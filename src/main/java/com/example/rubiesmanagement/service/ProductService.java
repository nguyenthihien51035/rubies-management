package com.example.rubiesmanagement.service;

import com.example.rubiesmanagement.dto.response.FilterProductResponse;
import com.example.rubiesmanagement.dto.response.ProductResponse;
import com.example.rubiesmanagement.form.product.FilterProductForm;
import com.example.rubiesmanagement.form.product.ProductForm;
import com.example.rubiesmanagement.model.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse createProduct(ProductForm form);

    ProductResponse getProductById(Integer id);

    void deleteProduct(Integer id);

    Page<FilterProductResponse> filterProducts(FilterProductForm filter);

    ProductResponse updateProduct(Integer id, ProductForm product);
}
