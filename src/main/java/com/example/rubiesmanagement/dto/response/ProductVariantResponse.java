package com.example.rubiesmanagement.dto.response;

import com.example.rubiesmanagement.enums.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductVariantResponse {
    private Integer id;
    private String colorName;
    private Size size;
    private Integer quantity;

    private List<ProductImageResponse> images;
}
