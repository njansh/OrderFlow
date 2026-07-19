package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class DeleteOrderUseCase {
    private final OrderRepository repo;
    private final UserRepository userRepo;
@
    public DeleteOrderUseCase(OrderRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public void execute(UUID id, User user) {
        var userAuth = userRepo.getUserById(user.getId());
        var order = repo.getOrderById(id);
        if (order == null) {
            throw new BusinessRuleException("Order not found");
        }
        if (userAuth == null) {
            throw new BusinessRuleException("User not found");
        }
        if (userAuth.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can delete orders.");
        }
        repo.delete(id);
    }
}
