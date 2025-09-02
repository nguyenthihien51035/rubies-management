package com.example.rubiesmanagement.repository;

import com.example.rubiesmanagement.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    boolean existsBySku(String sku);

    List<Product> findByCategoryId(Integer categoryId);

    List<Product> findTop4ByOrderByCreatedAtDesc();

}
