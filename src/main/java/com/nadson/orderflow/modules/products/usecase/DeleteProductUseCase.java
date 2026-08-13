package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class DeleteProductUseCase {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DeleteProductUseCase(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public void execute(UUID id, User authenticatedUser) {
        User user = userRepository.getUserById(authenticatedUser.getId());
        if (user == null) {
            throw new BusinessRuleException("User not found");
        }
        if (user.getRole() != Role.ADMIN) {
            throw new BusinessRuleException("Only admins can delete products");
        }
       if(productRepository.getProductById(id) == null) {
           throw new BusinessRuleException("Product not found");
       }
       productRepository.delete(id);

    }
}
