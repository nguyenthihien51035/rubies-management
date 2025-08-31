package com.example.rubiesmanagement.form.product;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FilterProductForm {
    private String name;
    private String sku;
    private Integer categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStock;
    private Boolean hasDiscount;
    private Integer page = 0;
    private Integer size = 10;
}
