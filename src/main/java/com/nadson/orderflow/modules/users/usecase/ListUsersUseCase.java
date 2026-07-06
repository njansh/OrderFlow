package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListUsersUseCase {
    private final UserRepository repo;

    public ListUsersUseCase(UserRepository repo) {
        this.repo = repo;
    }

    public List<User> execute() {
        return this.repo.listUsers();
    }

}
