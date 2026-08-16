package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateUserUseCase {
    private final UserRepository repo;

    public UpdateUserUseCase(UserRepository repo) {
        this.repo = repo;
    }

    public User execute(UserUpdateInput input, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new BusinessRuleException("Authenticated user not found");
        }

        var existingUser = repo.getUserById(input.id());
        if (existingUser == null) {
            throw new BusinessRuleException("User not found");
        }

        authenticatedUser.requireCanUpdateProfile(existingUser, input.role());

        User updatedUser = existingUser.updateData(input.name(), input.email(), input.role());

        return repo.update(updatedUser);
    }

    public record UserUpdateInput(UUID id, String name, String email, Role role) {}
}