package com.example.rubiesmanagement.form.user;

import com.example.rubiesmanagement.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserByAdminForm {

    @NotBlank(message = "Họ không được để trống")
    @Size(min = 2, max = 100, message = "Họ phải từ 2 đến 100 ký tự")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    private String lastName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[0-9]{9})$", message = "Số điện thoại phải bắt đầu bằng 0 và có 10 chữ số")
    private String phone;

    private Role role;
}
