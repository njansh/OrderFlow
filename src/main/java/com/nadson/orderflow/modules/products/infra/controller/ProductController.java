package com.nadson.orderflow.modules.products.infra.controller;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.infra.controller.dto.CreateProductRequest;
import com.nadson.orderflow.modules.products.infra.controller.dto.ProductResponse;
import com.nadson.orderflow.modules.products.usecase.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final CreateProductUseCase createProductUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final GetProductByIdUseCase getProductByIdUseCase;
    private final GetProductByNameUseCase getProductByNameUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;

    public ProductController(
            CreateProductUseCase createProductUseCase,
            ListProductsUseCase listProductsUseCase,
            GetProductByIdUseCase getProductByIdUseCase, GetProductByNameUseCase getProductByNameUseCase, DeleteProductUseCase deleteProductUseCase, UpdateProductUseCase updateProductUseCase
    ) {
        this.createProductUseCase = createProductUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.getProductByNameUseCase = getProductByNameUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid CreateProductRequest request) {
        Product product = createProductUseCase.execute(request.name(), request.price());
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

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

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) {
        Product product = getProductByIdUseCase.execute(id);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody @Valid CreateProductRequest request) {
        Product product = updateProductUseCase.execute(id,request.name(), request.price());
        return new ProductResponse(product.getId(), product.getName(), product.getPrice());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteProductUseCase.execute(id);
    }

}
