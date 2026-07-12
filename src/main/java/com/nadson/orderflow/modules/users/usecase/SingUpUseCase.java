package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.UserAlreadyExists;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SingUpUseCase {
    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public SingUpUseCase(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String name, String email, String senha) {
        User userExists = this.repo.getUserByEmail(email);

        if (userExists != null) {
            throw new UserAlreadyExists("User already exists");
        }

        String passwordHash = this.passwordEncoder.encode(senha);

        User user = User.createGuest(name, email, passwordHash);

        return this.repo.save(user);
    }
}
