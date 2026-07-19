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
    private final UserRepository userRepository;

    public GetOrderByStatusUseCase(OrderRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public List<Order> execute(OrderStatus status, User user) {
        var userAuth = userRepository.getUserById(user.getId());
        if (userAuth == null) {
            throw new BusinessRuleException("User not found");
        }

        if (userAuth.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can list orders by status.");
        }

        return repo.getOrdersByStatus(status);
    }
}
