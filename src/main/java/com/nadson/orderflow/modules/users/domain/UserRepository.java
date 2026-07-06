package com.nadson.orderflow.modules.users.domain;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User getUserByName(String username);
    User getUserByEmail(String email);
    User getUserById(UUID id);
    List<User> listUsers();
}
