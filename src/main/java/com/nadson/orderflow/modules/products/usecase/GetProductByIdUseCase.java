package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetProductByIdUseCase {
    private final ProductRepository repo;

    public GetProductByIdUseCase(ProductRepository repo) {
        this.repo = repo;
    }
    public Product execute(UUID id) {
        Product product = repo.getProductById(id);
        if (product == null) {
            throw new BusinessRuleException("Product not found");
        }
        return product;    }
}
