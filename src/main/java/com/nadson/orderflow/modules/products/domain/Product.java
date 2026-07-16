package com.nadson.orderflow.modules.products.domain;

import com.nadson.orderflow.shared.exception.BusinessRuleException;
import java.math.BigDecimal;
import java.util.UUID;

public class Product {
    private UUID id;
    private String name;
    private BigDecimal price;

    public Product(UUID id, String name, BigDecimal price) {
        validate(name, price);
        this.id = id == null ? UUID.randomUUID() : id;
        this.name = name;
        this.price = price;
    }

    private void validate(String name, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Product name cannot be null or blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Product price must be greater than zero");
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}