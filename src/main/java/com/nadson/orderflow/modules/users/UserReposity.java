package com.nadson.orderflow.modules.users;

import java.util.UUID;

public interface UserReposity {
    User save(User user);
    User getUserByName(String username);
    User getUserByEmail(String email);
    User getUserById(UUID id);
    User listUsers();
}
