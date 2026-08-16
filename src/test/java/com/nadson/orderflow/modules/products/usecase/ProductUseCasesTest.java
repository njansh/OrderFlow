package com.nadson.orderflow.modules.products.usecase;

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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCasesTest {

    @Mock
    private ProductRepository productRepo;

    private CreateProductUseCase createProductUseCase;
    private UpdateProductUseCase updateProductUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private GetProductByIdUseCase getProductByIdUseCase;
    private GetProductByNameUseCase getProductByNameUseCase;
    private ListProductsUseCase listProductsUseCase;

    private User adminUser;
    private User attendantUser;

    @BeforeEach
    void setUp() {
        createProductUseCase = new CreateProductUseCase(productRepo);
        updateProductUseCase = new UpdateProductUseCase(productRepo);
        deleteProductUseCase = new DeleteProductUseCase(productRepo);
        getProductByIdUseCase = new GetProductByIdUseCase(productRepo);
        getProductByNameUseCase = new GetProductByNameUseCase(productRepo);
        listProductsUseCase = new ListProductsUseCase(productRepo);

        adminUser = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        attendantUser = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);
    }

    @Test
    void shouldCreateProductWhenUserIsAdmin() {
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product product = createProductUseCase.execute("Burger", new BigDecimal("25.00"), adminUser);
        assertNotNull(product);
        assertEquals("Burger", product.getName());
    }

    @Test
    void shouldThrowWhenNonAdminTriesToCreateUpdateOrDelete() {
        UUID id = UUID.randomUUID();
        assertThrows(BusinessRuleException.class, () -> createProductUseCase.execute("Burger", BigDecimal.TEN, attendantUser));
        assertThrows(BusinessRuleException.class, () -> updateProductUseCase.execute(id, "Burger", BigDecimal.TEN, attendantUser));
        assertThrows(BusinessRuleException.class, () -> deleteProductUseCase.execute(id, attendantUser));
    }

    @Test
    void shouldUpdateProductWhenAdmin() {
        UUID id = UUID.randomUUID();
        Product product = new Product(id, "Old", BigDecimal.TEN);
        when(productRepo.getProductById(id)).thenReturn(product);
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updated = updateProductUseCase.execute(id, "New", new BigDecimal("15.00"), adminUser);
        assertEquals("New", updated.getName());
    }

    @Test
    void shouldThrowWhenUpdatingNonExistentProduct() {
        UUID id = UUID.randomUUID();
        when(productRepo.getProductById(id)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> updateProductUseCase.execute(id, "New", BigDecimal.TEN, adminUser));
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        UUID id = UUID.randomUUID();
        when(productRepo.getProductById(id)).thenReturn(new Product(id, "Burger", BigDecimal.TEN));

        deleteProductUseCase.execute(id, adminUser);
        verify(productRepo, times(1)).delete(id);
    }

    @Test
    void shouldThrowWhenDeletingNonExistentProduct() {
        UUID id = UUID.randomUUID();
        when(productRepo.getProductById(id)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> deleteProductUseCase.execute(id, adminUser));
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        UUID id = UUID.randomUUID();
        when(productRepo.getProductById(id)).thenReturn(new Product(id, "Burger", BigDecimal.TEN));
        Product result = getProductByIdUseCase.execute(id);
        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowWhenProductByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(productRepo.getProductById(id)).thenReturn(null);
        assertThrows(BusinessRuleException.class, () -> getProductByIdUseCase.execute(id));
    }

    @Test
    void shouldListProductsAndFindByName() {
        Product p = new Product(UUID.randomUUID(), "Burger", BigDecimal.TEN);
        when(listProductsUseCase.execute()).thenReturn(List.of(p));
        when(getProductByNameUseCase.execute("Burger")).thenReturn(List.of(p));

        assertEquals(1, listProductsUseCase.execute().size());
        assertEquals(1, getProductByNameUseCase.execute("Burger").size());
    }
}