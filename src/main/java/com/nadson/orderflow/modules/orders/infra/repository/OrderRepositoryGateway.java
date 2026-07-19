package com.nadson.orderflow.modules.orders.infra.repository;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.orders.infra.mapper.OrderMapper;
import com.nadson.orderflow.modules.orders.infra.persistence.OrderEntity;
import com.nadson.orderflow.modules.orders.infra.persistence.SpringDataOrderRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryGateway implements OrderRepository {
    private final SpringDataOrderRepository repo;
    private final OrderMapper mapper;

    public OrderRepositoryGateway(SpringDataOrderRepository repo, OrderMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order order) {
        return mapper.toDomain(repo.save(mapper.toEntity(order)));
    }

    @Override
    public Order getOrderById(UUID id) {
        return repo.findById(id).map(mapper::toDomain).orElse(null);
    }
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return repo.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> listOrders() {
        return repo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Order update(Order order) {
        return mapper.toDomain(repo.save(mapper.toEntity(order)));
    }

    @Override
    public void delete(UUID id) {
        repo.deleteById(id);
    }
}
