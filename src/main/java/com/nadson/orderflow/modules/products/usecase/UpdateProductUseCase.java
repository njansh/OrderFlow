package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public class UpdateProductUseCase {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public UpdateProductUseCase(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Product execute(UUID id, String name, BigDecimal price, User authenticatedUser) {
        User user = userRepository.getUserById(authenticatedUser.getId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admins can update products");
        }
        var product= productRepository.getProductById(id);
        if (product == null) {
            throw new BusinessRuleException("Product not found");
        }
        product.update(name, price);
        productRepository.save(product);

        return product;
    }
}
