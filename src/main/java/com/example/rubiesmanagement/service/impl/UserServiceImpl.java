package com.example.rubiesmanagement.service.impl;

import com.example.rubiesmanagement.dto.response.UserResponse;
import com.example.rubiesmanagement.exception.BusinessException;
import com.example.rubiesmanagement.exception.ErrorCodeConstant;
import com.example.rubiesmanagement.form.user.*;
import com.example.rubiesmanagement.model.User;
import com.example.rubiesmanagement.repository.UserRepository;
import com.example.rubiesmanagement.repository.specification.UserSpecification;
import com.example.rubiesmanagement.security.CustomUserDetails;
import com.example.rubiesmanagement.security.JwtUtil;
import com.example.rubiesmanagement.service.FileStorageService;
import com.example.rubiesmanagement.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public UserResponse register(CreateUserForm form, MultipartFile avatar) {
        if (userRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new BusinessException("Email đã tồn tại", ErrorCodeConstant.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.findByPhone(form.getPhone()).isPresent()) {
            throw new BusinessException("Số điện thoại đã tồn tại", ErrorCodeConstant.PHONE_ALREADY_EXISTS);
        }

        User user = modelMapper.map(form, User.class);
        user.setPassword(passwordEncoder.encode(form.getPassword()));

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = fileStorageService.storeFile(avatar, "users");
            user.setAvatarUrl(avatarUrl);
        }

        User saved = userRepository.save(user);
        return modelMapper.map(saved, UserResponse.class);
    }

    @Override
    public String login(LoginForm form) {
        User user = userRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new BusinessException("Thông tin đăng nhập không chính xác", ErrorCodeConstant.INVALID_LOGIN_CREDENTIALS));

        if (!user.isActive()) {
            throw new BusinessException("Tài khoản đã bị khóa", ErrorCodeConstant.USER_INACTIVE);
        }

        if (!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            throw new BusinessException("Thông tin đăng nhập không chính xác", ErrorCodeConstant.INVALID_LOGIN_CREDENTIALS);
        }

        // Tạo JWT
        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }

    @Override
    @Transactional
    public UserResponse updateUserbyAdmin(Integer id, UpdateUserByAdminForm form, MultipartFile avatar) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));

        if (!form.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(form.getEmail())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw new BusinessException("Email đã tồn tại", ErrorCodeConstant.EMAIL_ALREADY_EXISTS);
                    });
            user.setEmail(form.getEmail());
        }

        if (!form.getPhone().equals(user.getPhone())) {
            userRepository.findByPhone(form.getPhone())
                    .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                    .ifPresent(existingUser -> {
                        throw new BusinessException("Số điện thoại đã tồn tại", ErrorCodeConstant.PHONE_ALREADY_EXISTS);
                    });
            user.setPhone(form.getPhone());
        }

        if (!form.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(form.getFirstName());
        }

        if (!form.getLastName().equals(user.getLastName())) {
            user.setLastName(form.getLastName());
        }

        if (form.getRole() != null && !form.getRole().equals(user.getRole())) {
            user.setRole(form.getRole());
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = fileStorageService.storeFile(avatar, "users");
            user.setAvatarUrl(avatarUrl);
        } else if (form.getRemoveAvatar() != null && form.getRemoveAvatar()) {
            user.setAvatarUrl(null);
        }

        if (form.getActive() != null && !form.getActive().equals(user.isActive())) {
            user.setActive(form.getActive());
        }

        User updated = userRepository.save(user);
        return modelMapper.map(updated, UserResponse.class);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    @Transactional
    public void updateUserStatus(Integer id, boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));
        user.setActive(active);
        userRepository.save(user);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìn thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getMyProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails)) {
            throw new RuntimeException("Principal không phải là CustomUserDetails: " + principal.getClass().getName());
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(String email, UpdateUserForm form) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));

        userRepository.findByPhone(form.getPhone())
                .filter(existingUser -> !existingUser.getId().equals(user.getId()))
                .ifPresent(existingUser -> {
                    throw new BusinessException("Số điện thoại đã tồn tại", ErrorCodeConstant.PHONE_ALREADY_EXISTS);
                });

        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setPhone(form.getPhone());

        if (form.getAvatar() != null && !form.getAvatar().isEmpty()) {
            String avatarUrl = fileStorageService.storeFile(form.getAvatar(), "users");
            user.setAvatarUrl(avatarUrl);
        } else if (form.getRemoveAvatar() != null && form.getRemoveAvatar()) {
            user.setAvatarUrl(null);
        }

        User updated = userRepository.save(user);
        return modelMapper.map(updated, UserResponse.class);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng", ErrorCodeConstant.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException("Người dùng đã bị vô hiệu hóa", ErrorCodeConstant.USER_INACTIVE);
        }

        user.setActive(false);
        userRepository.save(user);
    }

    @Override
    public Page<UserResponse> filterUser(FilterUserForm filterUser) {
        Specification<User> specification = UserSpecification.firstNameContains(filterUser.getFirstName())
                .and(UserSpecification.lastNameContains(filterUser.getLastName()))
                .and(UserSpecification.emailEquals(filterUser.getEmail()))
                .and(UserSpecification.phoneEquals(filterUser.getPhone()))
                .and(UserSpecification.rolesEquals(filterUser.getRole()))
                .and(UserSpecification.isActive(filterUser.getActive()));

        Pageable pageable = PageRequest.of(filterUser.getPage(), filterUser.getSize());
        Page<User> users = userRepository.findAll(specification, pageable);

        return users.map(user -> modelMapper.map(user, UserResponse.class));
    }

    @Override
    public UserResponse changePassword(Authentication authentication, ChangePasswordForm form) {
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException("Người dùng chưa đăng nhập", ErrorCodeConstant.USER_IS_NOT_LOGIN);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác", ErrorCodeConstant.OLD_PASSWORD_IS_INCORRECT);
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);

        return modelMapper.map(user, UserResponse.class);
    }
}
