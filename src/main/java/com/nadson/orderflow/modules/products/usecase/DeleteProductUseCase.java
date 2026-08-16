package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class DeleteProductUseCase {
    private final ProductRepository productRepository;

    public DeleteProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(UUID id, User authenticatedUser) {
      authenticatedUser.requireAdmin();
       if(productRepository.getProductById(id) == null) {
           throw new BusinessRuleException("Product not found");
       }
       productRepository.delete(id);

    }
}
