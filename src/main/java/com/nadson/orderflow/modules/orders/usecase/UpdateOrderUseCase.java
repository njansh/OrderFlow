package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateOrderUseCase {
    private final OrderRepository repo;

    public UpdateOrderUseCase(OrderRepository repo) {
        this.repo = repo;
    }

    public Order execute(UUID orderId, String newStatus, User authenticatedUser) {
        Order order = repo.getOrderById(orderId);

        OrderStatus statusEnum;
        try {
            statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid status: " + newStatus);
        }

        order.changeStatus(statusEnum, authenticatedUser.getRole());

        return repo.update(order);
    }
}