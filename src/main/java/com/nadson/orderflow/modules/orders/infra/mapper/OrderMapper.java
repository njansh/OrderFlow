package com.nadson.orderflow.modules.orders.infra.mapper;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.infra.persistence.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper itemMapper;

    public OrderMapper(OrderItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    public Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        List<OrderItem> items = entity.getItems().stream()
                .map(itemMapper::toDomain)
                .collect(Collectors.toList());

        return new Order(
                entity.getId(),
                items,
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }

    public OrderEntity toEntity(Order order) {
        if (order == null) {
            return null;
        }
        return new OrderEntity(order);
    }
}