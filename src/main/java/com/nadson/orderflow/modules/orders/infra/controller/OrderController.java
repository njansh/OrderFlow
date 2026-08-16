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
import com.nadson.orderflow.shared.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Endpoints for order creation, lookup, and status transitions")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreatOrderUseCase creatOrderUseCase;
    private final GetOrderByIdUseCase getOrderByIdUseCase;
    private final UpdateOrderUseCase updateOrderUseCase;
    private final UserRepository userRepository;
    private final ListOrderUseCase listOrderUseCase;
    private final GetOrderByStatusUseCase getOrderByStatusUseCase;
    private final DeleteOrderUseCase deleteOrderUseCase;
    private final CurrentUserProvider currentUserProvider;

    public OrderController(
            CreatOrderUseCase creatOrderUseCase,
            GetOrderByIdUseCase getOrderByIdUseCase,
            UpdateOrderUseCase updateOrderUseCase,
            UserRepository userRepository,
            ListOrderUseCase listOrderUseCase,
            GetOrderByStatusUseCase getOrderByStatusUseCase,
            DeleteOrderUseCase deleteOrderUseCase, CurrentUserProvider currentUserProvider
    ) {
        this.creatOrderUseCase = creatOrderUseCase;
        this.getOrderByIdUseCase = getOrderByIdUseCase;
        this.updateOrderUseCase = updateOrderUseCase;
        this.userRepository = userRepository;
        this.listOrderUseCase = listOrderUseCase;
        this.getOrderByStatusUseCase = getOrderByStatusUseCase;
        this.deleteOrderUseCase = deleteOrderUseCase;
        this.currentUserProvider = currentUserProvider;
    }

    @Operation(summary = "Create order", description = "Creates a new order with a list of items. Requires ATTENDANT or ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Business rule failure or unauthorized role")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)

    public OrderResponse create(@RequestBody @Valid CreateOrderRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        List<CreatOrderUseCase.OrderInputItem> inputItems = request.items().stream()
                .map(item -> new CreatOrderUseCase.OrderInputItem(item.productId(), item.quantity()))
                .toList();

        Order order = creatOrderUseCase.execute(currentUser, inputItems);
        return OrderResponse.fromDomain(order);
    }

    @Operation(summary = "Get order by ID", description = "Retrieves full order details by its UUID.")
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable UUID id) {
        User currentUser = currentUserProvider.getCurrentUser();
        Order order = getOrderByIdUseCase.execute(id, currentUser);
        return OrderResponse.fromDomain(order);
    }

    @Operation(summary = "List orders", description = "Lists all orders or filters by status (PENDING, PREPARING, OUT_FOR_DELIVERY, COMPLETED, CANCELED).")
    @GetMapping
    public List<OrderResponse> list(@RequestParam(required = false) String status) {
        User currentUser = currentUserProvider.getCurrentUser();
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

    @Operation(summary = "Update order status", description = "Advances order status adhering to state machine rules and role permissions.")
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateOrderStatusRequest request
    ) {
        User currentUser = currentUserProvider.getCurrentUser();
        Order updatedOrder = updateOrderUseCase.execute(id, request.status(), currentUser);
        return OrderResponse.fromDomain(updatedOrder);
    }

    @Operation(summary = "Delete order", description = "Cancels or deletes an order by ID. Requires appropriate role permission.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        User currentUser = currentUserProvider.getCurrentUser();
        deleteOrderUseCase.execute(id, currentUser);
    }}

