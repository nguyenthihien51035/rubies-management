package com.example.rubiesmanagement.service.impl;


import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.form.user.ForgotPasswordForm;
import com.example.rubiesmanagement.form.user.ResetPasswordForm;
import com.example.rubiesmanagement.model.OtpToken;
import com.example.rubiesmanagement.model.User;
import com.example.rubiesmanagement.repository.OtpTokenRepository;
import com.example.rubiesmanagement.repository.UserRepository;
import com.example.rubiesmanagement.service.ForgotPasswordService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@AllArgsConstructor
public class ForgotPasswordImpl implements ForgotPasswordService {
    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(ForgotPasswordForm form) {
        User user = userRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new BusinessException("Email không tồn tại", ErrorCodeConstant.EMAIL_ALREADY_EXISTS));

        String otp = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);

        OtpToken token = new OtpToken();
        token.setEmail(user.getEmail());
        token.setOtp(otp);
        token.setExpirationTime(expiry);

        otpTokenRepository.save(token);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Mã OTP xác nhận quên mật khẩu");
        message.setText("Mã OTP của bạn là: " + otp + "\n\nLưu ý: OTP này có hiệu lực trong 5 phút.");
        mailSender.send(message);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordForm form) {
        OtpToken token = otpTokenRepository.findTopByEmailOrderByIdDesc(form.getEmail())
                .orElseThrow(() -> new BusinessException("OTP không hợp lệ", ErrorCodeConstant.INVALID_OTP));

        if (!token.getOtp().equals(form.getOtp())) {
            throw new BusinessException("OTP không chính xác", ErrorCodeConstant.INVALID_OTP);
        }

        if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP đã hết hạn", ErrorCodeConstant.INVALID_OTP);
        }

        User user = userRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new BusinessException("Người dùng không tồn tại", ErrorCodeConstant.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }
}
