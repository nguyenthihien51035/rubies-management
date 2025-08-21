package com.example.rubiesmanagement.repository.specification;

import com.example.rubiesmanagement.enums.Role;
import com.example.rubiesmanagement.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.trim().toLowerCase() + "%");
        };
    }

    public static Specification<User> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.trim().toLowerCase() + "%");
        };
    }


    public static Specification<User> emailEquals(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), email.toLowerCase());
        };
    }

    public static Specification<User> phoneEquals(String phone) {
        return (root, query, criteriaBuilder) -> {
            if (phone == null || phone.trim().isEmpty()) {
                return null;
            }
            return criteriaBuilder.equal(root.get("phone"), phone);
        };
    }

    public static Specification<User> rolesEquals(Role role) {
        return (root, query, criteriaBuilder) -> {
            if (role == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("role"), role);
        };
    }

    public static Specification<User> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> {
            if (isActive == null) return null;
            return criteriaBuilder.equal(root.get("active"), isActive);
        };
    }
}
