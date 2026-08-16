package com.nadson.orderflow.modules.orders.domain;

import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.shared.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private       OrderStatus status;

    public Order(List<OrderItem> items) {
        validate(items);
        this.id = UUID.randomUUID();
        this.items = items;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    public Order(UUID id, List<OrderItem> items, LocalDateTime createdAt, OrderStatus status) {
        validate(items);
        if (id == null) throw new BusinessRuleException("Id cannot be null");
        if (createdAt == null) throw new BusinessRuleException("CreatedAt cannot be null");
        if (status == null) throw new BusinessRuleException("Status cannot be null");

        this.id = id;
        this.items = items;
        this.createdAt = createdAt;
        this.status = status;
    }
    private void validate(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessRuleException("Order must have at least one item.");
        }
    }

    public BigDecimal total() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void changeStatus(OrderStatus newStatus, Role userRole) {
        if (newStatus == OrderStatus.CANCELED) {
            if (userRole == Role.GUEST || userRole == Role.DELIVERY) {
                throw new BusinessRuleException("Guests and delivery drivers cannot cancel orders.");
            }
            this.status = OrderStatus.CANCELED;
            return;
        }

        if (this.status == OrderStatus.PENDING && newStatus == OrderStatus.PREPARING) {
            if (userRole == Role.DELIVERY || userRole == Role.GUEST) {
                throw new BusinessRuleException("Delivery drivers and guests cannot prepare orders.");
            }
        } else if (this.status == OrderStatus.PREPARING && newStatus == OrderStatus.OUT_FOR_DELIVERY) {
            if (userRole == Role.ATTENDANT || userRole == Role.GUEST) {
                throw new BusinessRuleException("Attendants and guests cannot deliver orders.");
            }
        } else if (this.status == OrderStatus.OUT_FOR_DELIVERY && newStatus == OrderStatus.COMPLETED) {
            if (userRole != Role.DELIVERY && userRole != Role.ADMIN) {
                throw new BusinessRuleException("Only delivery personnel or admins can mark orders as delivered.");
            }

        } else {
            throw new BusinessRuleException("Invalid status transition.");
        }

        this.status = newStatus;
    }
    public void validateAccess(Role role) {
        if (role == Role.ADMIN) return;

        switch (role) {
            case DELIVERY -> {
                if (this.status != OrderStatus.OUT_FOR_DELIVERY && this.status != OrderStatus.COMPLETED) {
                    throw new BusinessRuleException("Delivery drivers can only access orders that are out for delivery or COMPLETED.");
                }
            }
            case ATTENDANT -> {
                if (this.status == OrderStatus.OUT_FOR_DELIVERY || this.status == OrderStatus.COMPLETED) {
                    throw new BusinessRuleException("Attendants cannot access orders currently in the delivery process.");
                }
            }
            case KITCHEN -> {
                if (this.status != OrderStatus.PREPARING && this.status != OrderStatus.PENDING) {
                    throw new BusinessRuleException("Kitchen staff can only access pending or preparing orders.");
                }
            }
            case GUEST -> throw new BusinessRuleException("Guests do not have permission to view order details.");
        }
    }
    public UUID getId() {
        return id;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}