package com.nadson.orderflow.modules.infra.persistence.users;

import com.nadson.orderflow.modules.users.Role;
import com.nadson.orderflow.modules.users.User;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private java.util.UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public UserEntity() {
    }

    public UserEntity(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
    }

    public java.util.UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

}
