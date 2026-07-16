package com.nadson.orderflow.modules.products.infra.repository;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.domain.ProductRepository;
import com.nadson.orderflow.modules.products.infra.mapper.ProductMapper;
import com.nadson.orderflow.modules.products.infra.persistence.ProductEntity;
import com.nadson.orderflow.modules.products.infra.persistence.SpringDataProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
@Component
public class ProductRepositoryGateway implements ProductRepository {
    private final SpringDataProductRepository repository;
    private final  ProductMapper mapper;

    public ProductRepositoryGateway(SpringDataProductRepository repository, ProductMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity= mapper.toEntity(product);
        ProductEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Product getProductById(UUID id) {
        return repository.findById(id).map(mapper::toDomain).orElse(null)        ;
    }

    @Override
    public List<Product> getProductByName(String name) {
return repository.findAllByName(name).stream().map(mapper::toDomain).toList();  }

    @Override
    public List<Product> listProducts() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Product update(Product product) {
        ProductEntity entity= mapper.toEntity(product);
        ProductEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);

    }



}
