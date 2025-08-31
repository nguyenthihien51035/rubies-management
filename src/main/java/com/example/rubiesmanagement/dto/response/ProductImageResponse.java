package com.example.rubiesmanagement.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponse {
    private Integer id;
    private String imageUrl;
    private Boolean isMain;
}
