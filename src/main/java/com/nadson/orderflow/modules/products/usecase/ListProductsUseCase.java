package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListProductsUseCase {
    private final ProductRepository repo;

    public ListProductsUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> execute() {
        return this.repo.listProducts();
    }
}
