package com.example.rubiesmanagement.service;

import com.example.rubiesmanagement.form.user.ForgotPasswordForm;
import com.example.rubiesmanagement.form.user.ResetPasswordForm;

public interface ForgotPasswordService {
    void sendOtp(ForgotPasswordForm form);

    void resetPassword(ResetPasswordForm form);
}
