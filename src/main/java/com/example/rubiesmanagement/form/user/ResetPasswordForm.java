package com.example.rubiesmanagement.form.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordForm {
    private String email;
    private String otp;
    private String newPassword;
}
