package com.nadson.orderflow.modules.orders.domain;

import java.util.List;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Order getOrderById(UUID id);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> listOrders();
    Order update(Order order);
    void delete(UUID id);
}