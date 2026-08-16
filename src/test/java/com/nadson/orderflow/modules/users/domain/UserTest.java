package com.nadson.orderflow.modules.users.domain;

import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserSuccessfully() {
        User user = new User(UUID.randomUUID(), "John", "john@orderflow.com", "password123", Role.ADMIN);
        assertEquals("John", user.getName());
        assertEquals("john@orderflow.com", user.getEmail());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void shouldThrowExceptionWhenCreatingUserWithInvalidData() {
        assertThrows(BusinessRuleException.class, () -> new User(null, "", "valid@email.com", "password123", Role.GUEST));
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "invalid-email", "password123", Role.GUEST));
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "valid@email.com", "short", Role.GUEST));
        assertThrows(BusinessRuleException.class, () -> new User(null, "Name", "valid@email.com", "password123", null));
    }

    @Test
    void shouldUpdateUserDataSuccessfully() {
        User user = User.createGuest("Old Name", "old@orderflow.com", "password123");
        User updated = user.updateData("New Name", "new@orderflow.com", Role.ATTENDANT);

        assertEquals("New Name", updated.getName());
        assertEquals("new@orderflow.com", updated.getEmail());
        assertEquals(Role.ATTENDANT, updated.getRole());
    }

    @Test
    void shouldValidateRoleRequirements() {
        User guest = User.createGuest("Guest", "guest@orderflow.com", "password123");
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "password123");
        User attendant = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "password123", Role.ATTENDANT);

        assertThrows(BusinessRuleException.class, guest::requireAdmin);
        assertDoesNotThrow(admin::requireAdmin);

        assertThrows(BusinessRuleException.class, guest::requireCanCreateOrders);
        assertDoesNotThrow(attendant::requireCanCreateOrders);
    }
}