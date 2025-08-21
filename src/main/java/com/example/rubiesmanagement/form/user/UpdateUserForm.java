package com.example.rubiesmanagement.form.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateUserForm {
    @NotBlank(message = "Họ không được để trống")
    @Size(min = 2, max = 100, message = "Họ phải từ 2 đến 100 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phone;

    private MultipartFile avatar;

    private Boolean removeAvatar;
}
