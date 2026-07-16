package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class DeleteProductUseCase {
    private final ProductRepository repo;

    public DeleteProductUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    public void execute(UUID id) {
       if(repo.getProductById(id) == null) {
           throw new BusinessRuleException("Product not found");
       }
       repo.delete(id);

    }
}
