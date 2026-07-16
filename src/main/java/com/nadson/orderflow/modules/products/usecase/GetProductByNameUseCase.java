package com.nadson.orderflow.modules.products.usecase;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductByNameUseCase {
    private final ProductRepository repo;

    public GetProductByNameUseCase(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> execute(String name) {
        return repo.getProductByName(name);
    }

}
