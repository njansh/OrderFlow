package com.nadson.orderflow.modules.users.usecase;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCasesTest {

    @Mock
    private UserRepository repo;

    private UpdateUserUseCase updateUserUseCase;
    private GetUserUseCase getUserUseCase;
    private ListUsersUseCase listUsersUseCase;

    private User adminUser;
    private User guestUser;

    @BeforeEach
    void setUp() {
        updateUserUseCase = new UpdateUserUseCase(repo);
        getUserUseCase = new GetUserUseCase(repo);
        listUsersUseCase = new ListUsersUseCase(repo);

        adminUser = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        guestUser = User.createGuest("Guest", "guest@orderflow.com", "passwordHash123");
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        when(repo.getUserById(adminUser.getId())).thenReturn(adminUser);
        User result = getUserUseCase.execute(adminUser.getId());
        assertEquals(adminUser.getId(), result.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UUID randomId = UUID.randomUUID();
        when(repo.getUserById(randomId)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> getUserUseCase.execute(randomId));
    }

    @Test
    void shouldListUsersSuccessfully() {
        when(repo.listUsers()).thenReturn(List.of(adminUser, guestUser));
        List<User> list = listUsersUseCase.execute(adminUser);
        assertEquals(2, list.size());
    }

    @Test
    void shouldAllowAdminToUpdateAnyUserAndRole() {
        when(repo.getUserById(guestUser.getId())).thenReturn(guestUser);
        when(repo.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateUserUseCase.UserUpdateInput input = new UpdateUserUseCase.UserUpdateInput(
                guestUser.getId(), "New Name", "new@orderflow.com", Role.ATTENDANT
        );

        User updated = updateUserUseCase.execute(input, adminUser);
        assertEquals("New Name", updated.getName());
        assertEquals(Role.ATTENDANT, updated.getRole());
    }
}