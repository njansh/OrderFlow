package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import com.nadson.orderflow.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderUseCasesTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private UserRepository userRepo;

    private CreatOrderUseCase creatOrderUseCase;
    private GetOrderByIdUseCase getOrderByIdUseCase;

    private User attendantUser;
    private User kitchenUser;
    private Product product;

    @BeforeEach
    void setUp() {
        creatOrderUseCase = new CreatOrderUseCase(orderRepo, productRepo);
        getOrderByIdUseCase = new GetOrderByIdUseCase(orderRepo);

        attendantUser = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);
        kitchenUser = new User(UUID.randomUUID(), "Kitchen Staff", "kitchen@orderflow.com", "passwordHash123", Role.KITCHEN);
        product = new Product(UUID.randomUUID(), "X-Burguer", new BigDecimal("20.00"));
    }

    @Test
    void shouldCreateOrderSuccessfullyAndCalculateTotal() {
        when(userRepo.getUserById(attendantUser.getId())).thenReturn(attendantUser);
        when(productRepo.getProductById(product.getId())).thenReturn(product);
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<CreatOrderUseCase.OrderInputItem> items = List.of(
                new CreatOrderUseCase.OrderInputItem(product.getId(), 2)
        );

        Order createdOrder = creatOrderUseCase.execute(attendantUser, items);

        assertNotNull(createdOrder);
        assertEquals(1, createdOrder.getItems().size());
        assertEquals(new BigDecimal("40.00"), createdOrder.total());
        verify(orderRepo, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenUserRoleCannotCreateOrder() {
        when(userRepo.getUserById(kitchenUser.getId())).thenReturn(kitchenUser);

        List<CreatOrderUseCase.OrderInputItem> items = List.of(
                new CreatOrderUseCase.OrderInputItem(product.getId(), 1)
        );

        assertThrows(BusinessRuleException.class, () ->
                creatOrderUseCase.execute(kitchenUser, items)
        );

        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderItemsAreEmpty() {
        when(userRepo.getUserById(attendantUser.getId())).thenReturn(attendantUser);

        assertThrows(BusinessRuleException.class, () ->
                creatOrderUseCase.execute(attendantUser, Collections.emptyList())
        );

        verify(orderRepo, never()).save(any(Order.class));
    }

    @Test
    void shouldGetOrderByIdSuccessfully() {
        Order mockOrder = new Order(List.of(
                new com.nadson.orderflow.modules.orders.domain.OrderItem(product.getId(), product.getName(), 1, product.getPrice())
        ));

        when(userRepo.getUserById(attendantUser.getId())).thenReturn(attendantUser);
        when(orderRepo.getOrderById(mockOrder.getId())).thenReturn(mockOrder);

        Order result = getOrderByIdUseCase.execute(mockOrder.getId(), attendantUser);

        assertNotNull(result);
        assertEquals(mockOrder.getId(), result.getId());
    }
}