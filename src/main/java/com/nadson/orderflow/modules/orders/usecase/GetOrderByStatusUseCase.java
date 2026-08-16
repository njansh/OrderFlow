package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetOrderByStatusUseCase {
    private final OrderRepository repo;

    public GetOrderByStatusUseCase(OrderRepository repo) {
        this.repo = repo;
    }

    public List<Order> execute(OrderStatus status, User authenticatedUser) {

        authenticatedUser.requireAdmin();

        return repo.getOrdersByStatus(status);
    }
}
