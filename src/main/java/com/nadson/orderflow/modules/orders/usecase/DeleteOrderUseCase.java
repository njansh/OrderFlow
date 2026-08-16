package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class DeleteOrderUseCase {
    private final OrderRepository repo;


    public DeleteOrderUseCase(OrderRepository repo) {
        this.repo = repo;
    }

    public void execute(UUID id, User authenticatedUser) {
        authenticatedUser.requireAdmin();
        var order = repo.getOrderById(id);
        if (order == null) {
            throw new BusinessRuleException("Order not found");
        }

        repo.delete(id);
    }
}
