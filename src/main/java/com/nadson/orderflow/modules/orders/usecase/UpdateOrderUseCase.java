package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrderUseCase {
    private final OrderRepository repo;
    private final UserRepository userRepo;
    public UpdateOrderUseCase(OrderRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }
public Order execute(Order order, User user) {
        var order1=repo.getOrderById(order.getId());
        if (order1 == null) {
            throw new BusinessRuleException("Order not found");
        }
var user1=userRepo.getUserById(user.getId());
        if (user1 == null) {
            throw new BusinessRuleException("User not found");
        }
        order.changeStatus(order.getStatus(), user1.getRole());
        return repo.update(order);
    }
}
