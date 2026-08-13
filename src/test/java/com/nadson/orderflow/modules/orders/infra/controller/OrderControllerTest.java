package com.nadson.orderflow.modules.orders.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.orders.infra.controller.dto.CreateOrderRequest;
import com.nadson.orderflow.modules.orders.infra.controller.dto.UpdateOrderStatusRequest;
import com.nadson.orderflow.modules.orders.usecase.*;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
 class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreatOrderUseCase creatOrderUseCase;

    @MockitoBean
    private GetOrderByIdUseCase getOrderByIdUseCase;

    @MockitoBean
    private UpdateOrderUseCase updateOrderUseCase;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ListOrderUseCase listOrderUseCase;

    @MockitoBean
    private GetOrderByStatusUseCase getOrderByStatusUseCase;

    @MockitoBean
    private DeleteOrderUseCase deleteOrderUseCase;

    @Test
    @WithMockUser(username = "attendant@orderflow.com")
    void shouldCreateOrderSuccessfully() throws Exception {
        User attendant = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);
        UUID productId = UUID.randomUUID();

        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest(productId, 2);
        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));

        OrderItem item = new OrderItem(productId, "Burguer", 2, new BigDecimal("20.00"));
        Order order = new Order(List.of(item));

        when(userRepository.getUserByEmail("attendant@orderflow.com")).thenReturn(attendant);
        when(creatOrderUseCase.execute(any(), any())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items.length()").value(1));
    }

    @Test
    @WithMockUser(username = "kitchen@orderflow.com")
    void shouldUpdateOrderStatusSuccessfully() throws Exception {
        User kitchen = new User(UUID.randomUUID(), "Kitchen", "kitchen@orderflow.com", "passwordHash123", Role.KITCHEN);
        UUID orderId = UUID.randomUUID();

        OrderItem item = new OrderItem(UUID.randomUUID(), "Burguer", 1, new BigDecimal("20.00"));
        Order order = new Order(List.of(item));
        order.changeStatus(OrderStatus.PREPARING, Role.KITCHEN);

        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest("PREPARING");

        when(userRepository.getUserByEmail("kitchen@orderflow.com")).thenReturn(kitchen);
        when(updateOrderUseCase.execute(eq(orderId), eq("PREPARING"), any())).thenReturn(order);

        mockMvc.perform(patch("/orders/{id}/status", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
    }
}