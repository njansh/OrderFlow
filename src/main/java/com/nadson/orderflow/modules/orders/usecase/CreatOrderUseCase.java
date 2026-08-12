package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreatOrderUseCase {
    private final OrderRepository repo;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CreatOrderUseCase(OrderRepository repo, ProductRepository productRepository, UserRepository userRepository) {
        this.repo = repo;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Order execute(User user, List<OrderInputItem> itemsInput) {
        var userAuth = userRepository.getUserById(user.getId());
        if (userAuth == null) {
            throw new BusinessRuleException("User not found");
        }

        if (userAuth.getRole() != Role.ADMIN && userAuth.getRole() != Role.ATTENDANT) {
            throw new BusinessRuleException("Only attendants and admins can create orders.");
        }

        if (itemsInput == null || itemsInput.isEmpty()) {
            throw new BusinessRuleException("An order must have at least one item");
        }

        List<OrderItem> items = itemsInput.stream().map(input -> {
            Product product = productRepository.getProductById(input.productId());

            if (product == null) {
                throw new BusinessRuleException("Product not found: " + input.productId());
            }

            return new OrderItem(
                    product.getId(),
                    product.getName(),
                    input.quantity(),
                    product.getPrice()
            );
        }).collect(Collectors.toList());

        Order order = new Order(items);

        return repo.save(order);
    }

    public record OrderInputItem(UUID productId, int quantity) {}
}
