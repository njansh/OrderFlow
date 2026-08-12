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
        var existingUser = repo.getUserById(input.id());
        if (existingUser == null) {
            throw new BusinessRuleException("User not found");
        }

        var currentUser = repo.getUserById(authenticatedUser.getId());
        if (currentUser == null) {
            throw new BusinessRuleException("Authenticated user not found");
        }

        if (currentUser.getRole() != Role.ADMIN) {
            if (!currentUser.getId().equals(existingUser.getId())) {
                throw new BusinessRuleException("Users can only update their own profile.");
            }
            if (input.role() != existingUser.getRole()) {
                throw new BusinessRuleException("Only admins can change user roles.");
            }
        }

        User updatedUser = existingUser.updateData(input.name(), input.email(), input.role());

        return repo.update(updatedUser);
    }

    public record UserUpdateInput(UUID id, String name, String email, Role role) {}
}