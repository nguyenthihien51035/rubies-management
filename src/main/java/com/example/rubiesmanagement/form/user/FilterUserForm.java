package com.example.rubiesmanagement.form.user;

import com.example.rubiesmanagement.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilterUserForm {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Role role;
    private Boolean active;

    private Integer page = 0;
    private Integer size = 10;
}
