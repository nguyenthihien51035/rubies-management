package com.example.rubiesmanagement.service;

import com.example.rubiesmanagement.dto.response.UserResponse;
import com.example.rubiesmanagement.form.user.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse register(@Valid CreateUserForm form, MultipartFile avatar);

    String login(@Valid LoginForm form);

    UserResponse updateUserbyAdmin(Integer id, UpdateUserByAdminForm form, MultipartFile avatar);

    Page<UserResponse> getAllUsers(Pageable pageable);

    void updateUserStatus(Integer id, boolean active);

    UserResponse getUserById(Integer id);

    UserResponse getMyProfile(Authentication authentication);

    UserResponse updateProfile(String email, UpdateUserForm form);

    void deleteUser(Integer id);

    Page<UserResponse> filterUser(FilterUserForm filterUser);

    UserResponse changePassword(Authentication authentication, ChangePasswordForm form);
}
