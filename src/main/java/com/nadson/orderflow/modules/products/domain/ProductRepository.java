package com.nadson.orderflow.modules.products.domain;

import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    Product save (Product product);
    Product getProductById(UUID id);
    List<Product> getProductByName(String name);
    List<Product> listProducts();


}
