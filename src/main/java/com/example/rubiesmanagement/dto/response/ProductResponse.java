package com.example.rubiesmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductResponse {
    private Integer id;
    private String name;
    private String sku;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Boolean inStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryResponse category;

    private List<ProductVariantResponse> variants;
}
