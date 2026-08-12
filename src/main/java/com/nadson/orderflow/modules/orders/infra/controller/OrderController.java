package com.nadson.orderflow.modules.orders.infra.controller;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.orders.infra.controller.dto.CreateOrderRequest;
import com.nadson.orderflow.modules.orders.infra.controller.dto.OrderResponse;
import com.nadson.orderflow.modules.orders.infra.controller.dto.UpdateOrderStatusRequest;
import com.nadson.orderflow.modules.orders.usecase.*;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreatOrderUseCase creatOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final UserRepository userRepository;
    private final ListOrderUseCase listOrderUseCase;
    private final GetOrderByStatusUseCase getOrderByStatusUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase; // Adicionado

    public OrderController(
            CreatOrderUseCase creatOrderUseCase,
            GetOrderByIdUseCase getOrderByIdUseCase,
            UpdateOrderUseCase updateOrderUseCase,
            UserRepository userRepository,
            ListOrderUseCase listOrderUseCase,
            GetOrderByStatusUseCase getOrderByStatusUseCase,
            DeleteOrderUseCase deleteOrderUseCase
    ) {
        this.creatOrderUseCase = creatOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.updateOrderUseCase = updateOrderUseCase;
        this.userRepository = userRepository;
        this.listOrderUseCase = listOrderUseCase;
        this.getOrderByStatusUseCase = getOrderByStatusUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        User currentUser = getAuthenticatedUser();

        List<CreatOrderUseCase.OrderInputItem> inputItems = request.items().stream()
                .map(item -> new CreatOrderUseCase.OrderInputItem(item.productId(), item.quantity()))
                .toList();

        Order order = creatOrderUseCase.execute(currentUser, inputItems);
        return OrderResponse.fromDomain(order);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        Order order = getOrderByIdUseCase.execute(id, currentUser);
        return OrderResponse.fromDomain(order);
    }

    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) String status) {
        User currentUser = getAuthenticatedUser();
        List<Order> orders;

        if (status != null && !status.isBlank()) {
            try {
                OrderStatus statusEnum = OrderStatus.valueOf(status.toUpperCase());
                orders = getOrderByStatusUseCase.execute(statusEnum, currentUser);
            } catch (IllegalArgumentException e) {
                throw new BusinessRuleException("Invalid order status: " + status);
            }
        } else {
            orders = listOrderUseCase.execute();
        }

        return orders.stream().map(OrderResponse::fromDomain).toList();
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateOrderStatusRequest request
    ) {
        User currentUser = getAuthenticatedUser();
        Order updatedOrder = updateOrderUseCase.execute(id, request.status(), currentUser);
        return OrderResponse.fromDomain(updatedOrder);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        deleteOrderUseCase.execute(id, currentUser);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.getUserByEmail(authentication.getName());
    }
}