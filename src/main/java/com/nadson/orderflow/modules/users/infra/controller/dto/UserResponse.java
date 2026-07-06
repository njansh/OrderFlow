package com.nadson.orderflow.modules.users.infra.controller.dto;

import com.nadson.orderflow.modules.users.domain.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String role
) {
    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
