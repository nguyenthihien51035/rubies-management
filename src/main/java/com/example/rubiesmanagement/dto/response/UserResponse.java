package com.example.rubiesmanagement.dto.response;

import com.example.rubiesmanagement.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Role role;
    private Boolean active;
}
