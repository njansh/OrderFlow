package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CreateProductUseCase {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    public CreateProductUseCase(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    public Product execute(String name, BigDecimal price, User authenticatedUser) {
        User user = userRepository.getUserById(authenticatedUser.getId());
        if (user == null) {
            throw new BusinessRuleException("User not found");
        }
        if (user.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can create products");
        }
        Product product = new Product(null, name, price);
        return productRepository.save(product);
    }
}
