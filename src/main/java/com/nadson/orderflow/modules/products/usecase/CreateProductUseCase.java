package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CreateProductUseCase {
    private final ProductRepository repo;
    public CreateProductUseCase(ProductRepository repo) {
        this.repo = repo;
    }
    public Product execute(String name, BigDecimal price) {
        Product product = new Product(null, name, price);
        return repo.save(product);
    }
}
