package com.example.rubiesmanagement.form.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class CategoryForm {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;

    @NotNull(message = "Ảnh danh mục không được để trống")
    private MultipartFile image;
}
