package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListOrderUseCase {
    private final OrderRepository repo;

    public ListOrderUseCase(OrderRepository repo) {
        this.repo = repo;
    }
    public List<Order> execute() {
        return repo.listOrders();
    }
}
