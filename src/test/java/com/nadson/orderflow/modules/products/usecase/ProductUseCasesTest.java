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

    private User adminUser;
    private User attendantUser;

    @BeforeEach
    void setUp() {
        createProductUseCase = new CreateProductUseCase(productRepo);
        updateProductUseCase = new UpdateProductUseCase(productRepo);
        deleteProductUseCase = new DeleteProductUseCase(productRepo);
        getProductByIdUseCase = new GetProductByIdUseCase(productRepo);

        adminUser = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        attendantUser = new User(UUID.randomUUID(), "Attendant", "attendant@orderflow.com", "passwordHash123", Role.ATTENDANT);
    }

    @Test
    void shouldCreateProductWhenUserIsAdmin() {
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product product = createProductUseCase.execute("X-Burger", new BigDecimal("25.00"), adminUser);

        assertNotNull(product);
        assertEquals("X-Burger", product.getName());
        assertEquals(new BigDecimal("25.00"), product.getPrice());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenNonAdminTriesToCreateProduct() {
        assertThrows(BusinessRuleException.class, () ->
                createProductUseCase.execute("X-Burger", new BigDecimal("25.00"), attendantUser)
        );

        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void shouldUpdateProductWhenUserIsAdmin() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product(productId, "Old Name", new BigDecimal("10.00"));

        when(productRepo.getProductById(productId)).thenReturn(existingProduct);
        when(productRepo.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = updateProductUseCase.execute(productId, "New Name", new BigDecimal("15.00"), adminUser);

        assertEquals("New Name", updatedProduct.getName());
        assertEquals(new BigDecimal("15.00"), updatedProduct.getPrice());
    }

    @Test
    void shouldDeleteProductWhenUserIsAdmin() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product(productId, "Burger", new BigDecimal("20.00"));

        when(productRepo.getProductById(productId)).thenReturn(existingProduct);

        deleteProductUseCase.execute(productId, adminUser);

        verify(productRepo, times(1)).delete(productId);
    }

    @Test
    void shouldGetProductByIdSuccessfully() {
        UUID productId = UUID.randomUUID();
        Product existingProduct = new Product(productId, "Burger", new BigDecimal("20.00"));

        when(productRepo.getProductById(productId)).thenReturn(existingProduct);

        Product result = getProductByIdUseCase.execute(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
    }
}