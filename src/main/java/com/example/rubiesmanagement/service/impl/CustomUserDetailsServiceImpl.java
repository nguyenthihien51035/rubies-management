package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.model.User;
import com.example.rubiesmanagement.repository.UserRepository;
import com.example.rubiesmanagement.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .filter(User::isActive)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));
    }
}
