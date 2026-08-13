package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.UserAlreadyExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SingUpUseCaseTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SingUpUseCase singUpUseCase;

    @BeforeEach
    void setUp() {
        singUpUseCase = new SingUpUseCase(repo, passwordEncoder);
    }

    @Test
    void shouldCreateFirstUserAsAdmin() {
        when(repo.getUserByEmail("admin@orderflow.com")).thenReturn(null);
        when(repo.listUsers()).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode("password123")).thenReturn("passwordHash123");
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = singUpUseCase.execute("Admin User", "admin@orderflow.com", "password123");

        assertNotNull(user);
        assertEquals(Role.ADMIN, user.getRole());
        assertEquals("admin@orderflow.com", user.getEmail());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void shouldCreateSubsequentUsersAsGuest() {
        User existingAdmin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");

        when(repo.getUserByEmail("guest@orderflow.com")).thenReturn(null);
        when(repo.listUsers()).thenReturn(List.of(existingAdmin));
        when(passwordEncoder.encode("password123")).thenReturn("passwordHash123");
        when(repo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = singUpUseCase.execute("Guest User", "guest@orderflow.com", "password123");

        assertNotNull(user);
        assertEquals(Role.GUEST, user.getRole());
        verify(repo, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        User existingUser = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");

        when(repo.getUserByEmail("admin@orderflow.com")).thenReturn(existingUser);

        assertThrows(UserAlreadyExists.class, () ->
                singUpUseCase.execute("Admin", "admin@orderflow.com", "password123")
        );

        verify(repo, never()).save(any(User.class));
    }
}