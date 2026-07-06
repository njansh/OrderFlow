package com.nadson.orderflow.modules.users.infra.mapper;

import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.infra.persistence.UserEntity;
import org.springframework.stereotype.Component;

@Component
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
