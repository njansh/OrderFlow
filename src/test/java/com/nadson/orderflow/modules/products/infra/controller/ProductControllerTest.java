package com.nadson.orderflow.modules.products.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.infra.controller.dto.CreateProductRequest;
import com.nadson.orderflow.modules.products.usecase.*;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.shared.security.CurrentUserProvider;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateProductUseCase createProductUseCase;

    @MockitoBean
    private ListProductsUseCase listProductsUseCase;

    @MockitoBean
    private GetProductByIdUseCase getProductByIdUseCase;

    @MockitoBean
    private GetProductByNameUseCase getProductByNameUseCase;

    @MockitoBean
    private DeleteProductUseCase deleteProductUseCase;

    @MockitoBean
    private UpdateProductUseCase updateProductUseCase;

    @MockitoBean
    private CurrentUserProvider currentUserProvider;

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldCreateProductSuccessfully() throws Exception {
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        Product product = new Product(UUID.randomUUID(), "Burger", new BigDecimal("25.00"));
        CreateProductRequest request = new CreateProductRequest("Burger", new BigDecimal("25.00"));

        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(createProductUseCase.execute(eq("Burger"), eq(new BigDecimal("25.00")), any())).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Burger"))
                .andExpect(jsonPath("$.price").value(25.00));
    }

    @Test
    @WithMockUser(username = "user@orderflow.com")
    void shouldListProductsWithoutQueryParam() throws Exception {
        Product product = new Product(UUID.randomUUID(), "Burger", new BigDecimal("25.00"));
        when(listProductsUseCase.execute()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Burger"));
    }

    @Test
    @WithMockUser(username = "user@orderflow.com")
    void shouldListProductsWithQueryParam() throws Exception {
        Product product = new Product(UUID.randomUUID(), "Burger", new BigDecimal("25.00"));
        when(getProductByNameUseCase.execute("Burger")).thenReturn(List.of(product));

        mockMvc.perform(get("/products").param("name", "Burger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Burger"));
    }

    @Test
    @WithMockUser(username = "user@orderflow.com")
    void shouldGetProductByIdSuccessfully() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Burger", new BigDecimal("25.00"));

        when(getProductByIdUseCase.execute(productId)).thenReturn(product);

        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Burger"));
    }

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldUpdateProductSuccessfully() throws Exception {
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Burger Turbo", new BigDecimal("30.00"));
        CreateProductRequest request = new CreateProductRequest("Burger Turbo", new BigDecimal("30.00"));

        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        when(updateProductUseCase.execute(eq(productId), eq("Burger Turbo"), eq(new BigDecimal("30.00")), any()))
                .thenReturn(product);

        mockMvc.perform(put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Burger Turbo"))
                .andExpect(jsonPath("$.price").value(30.00));
    }

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldDeleteProductSuccessfully() throws Exception {
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        UUID productId = UUID.randomUUID();

        when(currentUserProvider.getCurrentUser()).thenReturn(admin);
        doNothing().when(deleteProductUseCase).execute(eq(productId), any());

        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNoContent());
    }
}