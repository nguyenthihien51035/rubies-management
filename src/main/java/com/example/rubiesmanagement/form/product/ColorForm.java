package com.example.rubiesmanagement.form.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColorForm {
    @NotBlank(message = "Tên màu không được để trống")
    private String name;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "Mã màu không hợp lệ, mã màu phải có 6 ký tự")
    @NotBlank(message = "Mã hex không được để trống")
    private String hexCode;
}
