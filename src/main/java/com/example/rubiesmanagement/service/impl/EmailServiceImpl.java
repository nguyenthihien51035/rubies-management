package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Mã xác thực OTP - Rubies Fashion Store");
        message.setText("Mã OTP của bạn là: " + otp + "\n\nLưu ý: OTP này có hiệu lực trong 5 phút.");

        mailSender.send(message);
    }
}
