package com.nadson.orderflow.modules.users.infra.repository;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.modules.users.infra.mapper.UserMapper;
import com.nadson.orderflow.modules.users.infra.persistence.SpringDataUserRepository;
import com.nadson.orderflow.modules.users.infra.persistence.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserRepositoryGateway implements UserRepository {
    private final SpringDataUserRepository repo;
    private final UserMapper mapper;

    public UserRepositoryGateway(SpringDataUserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = repo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public User getUserByName(String username) {
        // Implementation depends on adding findByName to SpringDataUserRepository
        // Returning null for now as per current JPA interface capabilities
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return repo.findByEmail(email)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public User getUserById(UUID id) {
        return repo.findById(id)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public List<User> listUsers() {
        return repo.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
