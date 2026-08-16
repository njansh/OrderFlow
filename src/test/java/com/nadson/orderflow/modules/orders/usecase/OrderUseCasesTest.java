package com.nadson.orderflow.modules.orders.usecase;

import com.nadson.orderflow.modules.orders.domain.Order;
import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.domain.OrderRepository;
import com.nadson.orderflow.modules.orders.domain.OrderStatus;
import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.users.domain.Role;
import com.nadson.orderflow.modules.users.domain.User;
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

    private CreatOrderUseCase creatOrderUseCase;
    private GetOrderByIdUseCase getOrderByIdUseCase;
    private GetOrderByStatusUseCase getOrderByStatusUseCase;
    private ListOrderUseCase listOrderUseCase;
    private DeleteOrderUseCase deleteOrderUseCase;

    private User adminUser;
    private User attendantUser;
    private User guestUser;
    private Product product;

    @BeforeEach
    void setUp() {
        creatOrderUseCase = new CreatOrderUseCase(orderRepo, productRepo);
        getOrderByIdUseCase = new GetOrderByIdUseCase(orderRepo);
        getOrderByStatusUseCase = new GetOrderByStatusUseCase(orderRepo);
        listOrderUseCase = new ListOrderUseCase(orderRepo);
        deleteOrderUseCase = new DeleteOrderUseCase(orderRepo);

        adminUser = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        attendantUser = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);
        guestUser = User.createGuest("Guest", "guest@orderflow.com", "passwordHash123");
        product = new Product(UUID.randomUUID(), "Burger", new BigDecimal("20.00"));
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        when(productRepo.getProductById(product.getId())).thenReturn(product);
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = creatOrderUseCase.execute(attendantUser, List.of(new CreatOrderUseCase.OrderInputItem(product.getId(), 2)));
        assertNotNull(order);
        assertEquals(new BigDecimal("40.00"), order.total());
    }

    @Test
    void shouldThrowWhenCreatingOrderWithMissingProductOrEmptyItems() {
        UUID nonExistent = UUID.randomUUID();
        when(productRepo.getProductById(nonExistent)).thenReturn(null);

        assertThrows(BusinessRuleException.class, () ->
                creatOrderUseCase.execute(attendantUser, List.of(new CreatOrderUseCase.OrderInputItem(nonExistent, 1)))
        );
        assertThrows(BusinessRuleException.class, () -> creatOrderUseCase.execute(attendantUser, Collections.emptyList()));
        assertThrows(BusinessRuleException.class, () -> creatOrderUseCase.execute(guestUser, List.of(new CreatOrderUseCase.OrderInputItem(product.getId(), 1))));
    }

    @Test
    void shouldGetOrderByIdAndHandleErrors() {
        Order mockOrder = new Order(List.of(new OrderItem(product.getId(), "Burger", 1, product.getPrice())));
        when(orderRepo.getOrderById(mockOrder.getId())).thenReturn(mockOrder);

        Order found = getOrderByIdUseCase.execute(mockOrder.getId(), adminUser);
        assertEquals(mockOrder.getId(), found.getId());

        UUID randomId = UUID.randomUUID();
        when(orderRepo.getOrderById(randomId)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> getOrderByIdUseCase.execute(randomId, adminUser));
        assertThrows(BusinessRuleException.class, () -> getOrderByIdUseCase.execute(mockOrder.getId(), null));
    }

    @Test
    void shouldListOrdersAndFilterByStatus() {
        Order mockOrder = new Order(List.of(new OrderItem(product.getId(), "Burger", 1, product.getPrice())));
        when(listOrderUseCase.execute()).thenReturn(List.of(mockOrder));
        when(orderRepo.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(List.of(mockOrder));

        assertEquals(1, listOrderUseCase.execute().size());
        assertEquals(1, getOrderByStatusUseCase.execute(OrderStatus.PENDING, adminUser).size());
        assertThrows(BusinessRuleException.class, () -> getOrderByStatusUseCase.execute(OrderStatus.PENDING, guestUser));
    }

    @Test
    void shouldDeleteOrderWhenAdminAndThrowWhenNotFoundOrNonAdmin() {
        UUID id = UUID.randomUUID();
        when(orderRepo.getOrderById(id)).thenReturn(new Order(List.of(new OrderItem(product.getId(), "Burger", 1, product.getPrice()))));

        deleteOrderUseCase.execute(id, adminUser);
        verify(orderRepo, times(1)).delete(id);

        UUID nonExistent = UUID.randomUUID();
        when(orderRepo.getOrderById(nonExistent)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> deleteOrderUseCase.execute(nonExistent, adminUser));
        assertThrows(BusinessRuleException.class, () -> deleteOrderUseCase.execute(id, guestUser));
    }
}