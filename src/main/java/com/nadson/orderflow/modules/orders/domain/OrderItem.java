package com.nadson.orderflow.modules.orders.domain;


import com.nadson.orderflow.shared.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {
    private final UUID productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal price;

    public OrderItem(UUID productId, String productName, int quantity, BigDecimal price) {
        validate(productId, productName, quantity, price);
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    private void validate(UUID productId, String productName, int quantity, BigDecimal price) {
        if(productId == null){
            throw new BusinessRuleException("Product ID cannot be null");
        }
        if(productName == null || productName.isBlank()){
            throw new BusinessRuleException("Product name cannot be null or blank");
            }
        if(quantity <= 0){
            throw new BusinessRuleException("Quantity must be greater than zero");

        }
        if(price == null || price.compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessRuleException("Price must be greater than zero");
        }
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public BigDecimal getSubtotal() {
        return this.price.multiply(BigDecimal.valueOf(this.quantity));
    }
}