package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetUserUseCase {
    private final UserRepository repo;

    public GetUserUseCase(UserRepository repo) {
        this.repo = repo;
    }

    public User execute(UUID id) {
        User user = this.repo.getUserById(id);

        if (user == null) {
            throw new BusinessRuleException("User not found");
        }

        return user;
    }

}
