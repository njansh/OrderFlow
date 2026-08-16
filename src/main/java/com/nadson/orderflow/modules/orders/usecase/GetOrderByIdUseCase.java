package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetOrderByIdUseCase {
    private final OrderRepository repo;

    public GetOrderByIdUseCase(OrderRepository repo) {
        this.repo = repo;
    }

    public Order execute(UUID id, User authenticatedUser) {
        if (authenticatedUser == null) {
            throw new BusinessRuleException("User not authenticated");
        }

        Order order = repo.getOrderById(id);
        if (order == null) {
            throw new BusinessRuleException("Order not found with ID: " + id);
        }
        order.validateAccess(authenticatedUser.getRole());

        return order;
    }

}
