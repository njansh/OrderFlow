package com.nadson.orderflow.modules.users.domain;

import com.nadson.orderflow.shared.exception.BusinessRuleException;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;

    public User(UUID id, String name, String email, String password, Role role) {
        validate(name, email, password, role);
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static User createGuest(String name, String email, String password) {
        return new User(null, name, email, password, Role.GUEST);
    }

    public static User createAdmin(String name, String email, String passwordHash) {
        return new User(null, name, email, passwordHash, Role.ADMIN);
    }

    private void validate(String name, String email, String password, Role role) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("name can't be null or blank");
        }

        if (email == null || email.isBlank()) {
            throw new BusinessRuleException("email can't be null or blank");
        }

        if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new BusinessRuleException("email must be a valid format");
        }

        if (password == null || password.isBlank() || password.length() < 8) {
            throw new BusinessRuleException("password can't be null or blank and must have at least 8 characters");
        }
        if (role == null) {
            throw new BusinessRuleException("role can't be null");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}
