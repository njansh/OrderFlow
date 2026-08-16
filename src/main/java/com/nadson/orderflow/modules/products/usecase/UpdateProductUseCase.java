package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
@Service
public class UpdateProductUseCase {
    private final ProductRepository productRepository;

    public UpdateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(UUID id, String name, BigDecimal price, User authenticatedUser) {
       authenticatedUser.requireAdmin();
        var product= productRepository.getProductById(id);
        if (product == null) {
            throw new BusinessRuleException("Product not found");
        }
        product.update(name, price);
        productRepository.save(product);

        return product;
    }

}
