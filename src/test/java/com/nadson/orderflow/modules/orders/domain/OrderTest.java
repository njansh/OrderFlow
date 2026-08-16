package com.nadson.orderflow.modules.orders.domain;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCalculateTotalAndAccessProperties() {
        OrderItem item1 = new OrderItem(UUID.randomUUID(), "Burger", 2, new BigDecimal("20.00"));
        OrderItem item2 = new OrderItem(UUID.randomUUID(), "Fries", 1, new BigDecimal("10.00"));
        Order order = new Order(List.of(item1, item2));

        assertEquals(new BigDecimal("50.00"), order.total());
        assertNotNull(order.getId());
        assertNotNull(order.getCreatedAt());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(2, order.getItems().size());
    }

    @Test
    void shouldReconstructOrderWithAllParameters() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        OrderItem item = new OrderItem(UUID.randomUUID(), "Burger", 1, new BigDecimal("20.00"));
        Order order = new Order(id, List.of(item), now, OrderStatus.PREPARING);

        assertEquals(id, order.getId());
        assertEquals(OrderStatus.PREPARING, order.getStatus());
    }

    @Test
    void shouldValidateAccessByRole() {
        OrderItem item = new OrderItem(UUID.randomUUID(), "Burger", 1, new BigDecimal("20.00"));
        Order pendingOrder = new Order(List.of(item));

        assertDoesNotThrow(() -> pendingOrder.validateAccess(Role.ADMIN));
        assertDoesNotThrow(() -> pendingOrder.validateAccess(Role.ATTENDANT));
        assertDoesNotThrow(() -> pendingOrder.validateAccess(Role.KITCHEN));
        assertThrows(BusinessRuleException.class, () -> pendingOrder.validateAccess(Role.DELIVERY));
        assertThrows(BusinessRuleException.class, () -> pendingOrder.validateAccess(Role.GUEST));
    }

    @Test
    void shouldValidateOrderItem() {
        UUID productId = UUID.randomUUID();
        assertThrows(BusinessRuleException.class, () -> new OrderItem(null, "Burger", 1, new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> new OrderItem(productId, "", 1, new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> new OrderItem(productId, "Burger", 0, new BigDecimal("20.00")));
        assertThrows(BusinessRuleException.class, () -> new OrderItem(productId, "Burger", 1, BigDecimal.ZERO));
    }
}