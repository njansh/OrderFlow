package com.nadson.orderflow.modules.users.infra.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateUserRequest(
        UUID id,
        @NotBlank String name,
        @NotBlank @Email String email,
         String role

) {
}
