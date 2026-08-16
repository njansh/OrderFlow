package com.nadson.orderflow.modules.users.domain;

import com.nadson.orderflow.shared.exception.BusinessRuleException;
import java.util.UUID;
import java.util.regex.Pattern;

public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    private final UUID id;
    private final String name;
    private final String email;
    private final String password;
    private final Role role;

    public User(UUID id, String name, String email, String password, Role role) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);
        validateRole(role);

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

    public User updateData(String newName, String newEmail, Role newRole) {
        validateName(newName);
        validateEmail(newEmail);
        validateRole(newRole);
        return new User(this.id, newName, newEmail, this.password, newRole);
    }

    public void requireAdmin() {
        if (this.role != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can perform this action");
        }
    }

    public void requireCanCreateOrders() {
        if (this.role != Role.ADMIN && this.role != Role.ATTENDANT) {
            throw new BusinessRuleException("Only attendants and admins can create orders.");
        }
    }
    public void requireCanUpdateProfile(User targetUser, Role newRole) {
        if (this.role == Role.ADMIN) {
            return;
        }

        if (!this.id.equals(targetUser.getId())) {
            throw new BusinessRuleException("Users can only update their own profile.");
        }

        if (newRole != targetUser.getRole()) {
            throw new BusinessRuleException("Only admins can change user roles.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) throw new BusinessRuleException("name can't be null or blank");
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) throw new BusinessRuleException("email can't be null or blank");
        if (!EMAIL_PATTERN.matcher(email).matches()) throw new BusinessRuleException("email must be a valid format");
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() < 8) {
            throw new BusinessRuleException("password can't be null or blank and must have at least 8 characters");
        }
    }

    private void validateRole(Role role) {
        if (role == null) throw new BusinessRuleException("role can't be null");
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
}