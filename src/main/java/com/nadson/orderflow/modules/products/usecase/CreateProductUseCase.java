package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class CreateProductUseCase {
    private final ProductRepository productRepository;
    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    public Product execute(String name, BigDecimal price, User authenticatedUser) {

      authenticatedUser.requireAdmin();
        Product product = new Product(null, name, price);
        return productRepository.save(product);
    }
}
