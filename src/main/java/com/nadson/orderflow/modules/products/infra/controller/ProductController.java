package com.nadson.orderflow.modules.products.infra.controller;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.infra.controller.dto.CreateProductRequest;
import com.nadson.orderflow.modules.products.infra.controller.dto.ProductResponse;
import com.nadson.orderflow.modules.products.usecase.*;
import com.nadson.orderflow.modules.users.domain.User;
import com.nadson.orderflow.modules.users.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Products", description = "Endpoints for managing the product catalog")
@RestController
@RequestMapping("/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetProductByNameUseCase getProductByNameUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UserRepository userRepository;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            ListProductsUseCase listProductsUseCase,
            GetProductByIdUseCase getProductByIdUseCase,
            GetProductByNameUseCase getProductByNameUseCase,
            DeleteProductUseCase deleteProductUseCase,
            UpdateProductUseCase updateProductUseCase,
            UserRepository userRepository
    ) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getProductByNameUseCase = getProductByNameUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.getUserByEmail(authentication.getName());
    }

    @Operation(summary = "Create product", description = "Creates a new product in the catalog. Requires ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Unauthorized role or invalid input")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid CreateProductRequest request) {
        User currentUser = getAuthenticatedUser();
        Product product = createProductUseCase.execute(request.name(), request.price(), currentUser);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

    @Operation(summary = "List products", description = "Returns all products or filters by name if search query is provided.")
    @GetMapping
    public List<ProductResponse> list(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return getProductByNameUseCase.execute(name)
                    .stream()
                    .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                    .toList();
        } else {
            return listProductsUseCase.execute().stream()
                    .map(p -> new ProductResponse(p.getId(), p.getName(), p.getPrice()))
                    .toList();
        }
    }

    @Operation(summary = "Get product by ID", description = "Retrieves product details by its unique UUID.")
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        Product product = getProductByIdUseCase.execute(id);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

    @Operation(summary = "Update product", description = "Updates product name and price. Requires ADMIN role.")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody @Valid CreateProductRequest request) {
        User currentUser = getAuthenticatedUser();
        Product product = updateProductUseCase.execute(id, request.name(), request.price(), currentUser);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID. Requires ADMIN role.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        User currentUser = getAuthenticatedUser();
        deleteProductUseCase.execute(id, currentUser);
    }
}