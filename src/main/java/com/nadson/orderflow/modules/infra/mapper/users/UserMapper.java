package com.nadson.orderflow.modules.infra.mapper.users;

import com.nadson.orderflow.modules.infra.persistence.users.UserEntity;
import com.nadson.orderflow.modules.users.User;

public class UserMapper {
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserEntity(user);
    }

    public User toDomain(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        return new User(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRole()
        );
    }
}
