package com.nadson.orderflow.modules.products.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findAllByName(String name);

}
