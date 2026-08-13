package com.nadson.orderflow.modules.products.infra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.infra.controller.dto.CreateProductRequest;
import com.nadson.orderflow.modules.products.usecase.*;
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
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldCreateProductSuccessfully() throws Exception {
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        Product product = new Product(UUID.randomUUID(), "Burguer", new BigDecimal("25.00"));
        CreateProductRequest request = new CreateProductRequest("Burguer", new BigDecimal("25.00"));

        when(userRepository.getUserByEmail("admin@orderflow.com")).thenReturn(admin);
        when(createProductUseCase.execute(eq("Burguer"), eq(new BigDecimal("25.00")), any())).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Burguer"))
                .andExpect(jsonPath("$.price").value(25.00));
    }

    @Test
    @WithMockUser(username = "user@orderflow.com")
    void shouldListProductsSuccessfully() throws Exception {
        Product product = new Product(UUID.randomUUID(), "Burguer", new BigDecimal("25.00"));
        when(listProductsUseCase.execute()).thenReturn(List.of(product));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Burguer"));
    }

    @Test
    @WithMockUser(username = "admin@orderflow.com")
    void shouldDeleteProductSuccessfully() throws Exception {
        User admin = User.createAdmin("Admin", "admin@orderflow.com", "passwordHash123");
        UUID productId = UUID.randomUUID();

        when(userRepository.getUserByEmail("admin@orderflow.com")).thenReturn(admin);
        doNothing().when(deleteProductUseCase).execute(eq(productId), any());

        mockMvc.perform(delete("/products/{id}", productId))
                .andExpect(status().isNoContent());
    }
}