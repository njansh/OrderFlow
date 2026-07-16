package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public class UpdateProductUseCase {
    private final ProductRepository repo;

    public UpdateProductUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    public Product execute(UUID id, String name, BigDecimal price) {
        var product= repo.getProductById(id);
        if (product == null) {
            throw new BusinessRuleException("Product not found");
        }
        product.update(name, price);
        repo.save(product);

        return product;
    }
}
