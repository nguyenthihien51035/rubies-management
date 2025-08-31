package com.example.rubiesmanagement.form.product;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductImageForm {
    private Integer Id;
    private MultipartFile imageUrl;
    private Boolean isMain;

}
