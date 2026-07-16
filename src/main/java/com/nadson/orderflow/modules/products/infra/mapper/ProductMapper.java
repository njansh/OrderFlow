package com.nadson.orderflow.modules.products.infra.mapper;

import com.nadson.orderflow.modules.products.domain.Product;
import com.nadson.orderflow.modules.products.infra.persistence.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toDomain(ProductEntity entity){
        if (entity == null) {
            return null;
        }
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getPrice()
        );

    }
    public ProductEntity toEntity(Product product){
        if (product == null) {
            return null;
        }
        return new ProductEntity(product);
    }



}
