package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetOrderByIdUseCase {
    private final OrderRepository repo;
    private final UserRepository userRepository;

    public GetOrderByIdUseCase(OrderRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public Order execute(UUID id, User user) {
        var userAuth = userRepository.getUserById(user.getId());
        if (userAuth == null) {
            throw new BusinessRuleException("User not found");
        }

        Order order = repo.getOrderById(id);
        if (order == null) {
            throw new BusinessRuleException("Order not found with ID: " + id);
        }

        validateAccess(order, userAuth.getRole());

        return order;
    }

    private void validateAccess(Order order, Role role) {
        if (role == Role.ADMIN) return;

        OrderStatus status = order.getStatus();

        if (role == Role.DELIVERY) {
            if (status != OrderStatus.OUT_FOR_DELIVERY && status != OrderStatus.DELIVERED) {
                throw new BusinessRuleException("Delivery drivers can only access orders that are out for delivery or delivered.");
            }
        } else if (role == Role.ATTENDANT) {
            if (status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED) {
                throw new BusinessRuleException("Attendants cannot access orders currently in the delivery process.");
            }
        } else if (role == Role.KITCHEN) {
            if (status != OrderStatus.PREPARING && status != OrderStatus.PENDING) {
                throw new BusinessRuleException("Kitchen staff can only access pending or preparing orders.");
            }
        } else if (role == Role.GUEST) {
            throw new BusinessRuleException("Guests do not have permission to view order details.");
        }
    }
}
