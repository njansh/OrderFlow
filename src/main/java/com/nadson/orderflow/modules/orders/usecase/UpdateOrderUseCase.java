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
    private final UserRepository userRepo;

    public UpdateOrderUseCase(OrderRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public Order execute(UUID orderId, String newStatus, User authenticatedUser) {
        Order order = repo.getOrderById(orderId);
        if (order == null) {
            throw new BusinessRuleException("Order not found");
        }

        User user = userRepo.getUserById(authenticatedUser.getId());
        if (user == null) {
            throw new BusinessRuleException("User not found");
        }

        OrderStatus statusEnum;
        try {
            statusEnum = OrderStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid status: " + newStatus);
        }

        order.changeStatus(statusEnum, user.getRole());

        return repo.update(order);
    }
}