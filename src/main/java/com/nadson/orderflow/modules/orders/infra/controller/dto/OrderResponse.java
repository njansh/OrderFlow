package com.nadson.orderflow.modules.orders.infra.controller.dto;

import com.nadson.orderflow.modules.orders.domain.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        List<OrderItemResponse> items,
        String status,
        BigDecimal total,
        LocalDateTime createdAt
) {
    public record OrderItemResponse(
            UUID productId,
            String productName,
            int quantity,
            BigDecimal price,
            BigDecimal subtotal
    ) {}

    // Método facilitador para converter o Domínio para o DTO de Resposta
    public static OrderResponse fromDomain(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal()
                )).toList();

        return new OrderResponse(
                order.getId(),
                itemResponses,
                order.getStatus().name(),
                order.total(),
                order.getCreatedAt()
        );
    }
}