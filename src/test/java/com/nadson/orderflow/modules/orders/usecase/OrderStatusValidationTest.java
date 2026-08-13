package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusValidationTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private UserRepository userRepo;

    private UpdateOrderUseCase updateOrderUseCase;

    private User kitchenUser;
    private User deliveryUser;
    private User attendantUser;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        updateOrderUseCase = new UpdateOrderUseCase(orderRepo, userRepo);

        kitchenUser = new User(UUID.randomUUID(), "Kitchen", "kitchen@orderflow.com", "passwordHash123", Role.KITCHEN);
        deliveryUser = new User(UUID.randomUUID(), "Delivery", "delivery@orderflow.com", "passwordHash123", Role.DELIVERY);
        attendantUser = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);

        OrderItem item = new OrderItem(UUID.randomUUID(), "Product", 1, new BigDecimal("10.00"));
        pendingOrder = new Order(List.of(item));
    }

    @Test
    void shouldAllowKitchenToAdvanceOrderToPreparing() {
        when(orderRepo.getOrderById(pendingOrder.getId())).thenReturn(pendingOrder);
        when(userRepo.getUserById(kitchenUser.getId())).thenReturn(kitchenUser);
        when(orderRepo.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updatedOrder = updateOrderUseCase.execute(pendingOrder.getId(), "PREPARING", kitchenUser);

        assertEquals(OrderStatus.PREPARING, updatedOrder.getStatus());
        verify(orderRepo, times(1)).update(any(Order.class));
    }

    @Test
    void shouldAllowAttendantToCancelPendingOrder() {
        when(orderRepo.getOrderById(pendingOrder.getId())).thenReturn(pendingOrder);
        when(userRepo.getUserById(attendantUser.getId())).thenReturn(attendantUser);
        when(orderRepo.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order updatedOrder = updateOrderUseCase.execute(pendingOrder.getId(), "CANCELED", attendantUser);

        assertEquals(OrderStatus.CANCELED, updatedOrder.getStatus());
        verify(orderRepo, times(1)).update(any(Order.class));
    }

    @Test
    void shouldBlockDeliveryUserFromAdvancingOrderToPreparing() {
        when(orderRepo.getOrderById(pendingOrder.getId())).thenReturn(pendingOrder);
        when(userRepo.getUserById(deliveryUser.getId())).thenReturn(deliveryUser);

        assertThrows(BusinessRuleException.class, () ->
                updateOrderUseCase.execute(pendingOrder.getId(), "PREPARING", deliveryUser)
        );

        verify(orderRepo, never()).update(any(Order.class));
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        when(orderRepo.getOrderById(pendingOrder.getId())).thenReturn(pendingOrder);
        when(userRepo.getUserById(deliveryUser.getId())).thenReturn(deliveryUser);

        assertThrows(BusinessRuleException.class, () ->
                updateOrderUseCase.execute(pendingOrder.getId(), "DELIVERED", deliveryUser)
        );

        verify(orderRepo, never()).update(any(Order.class));
    }
}