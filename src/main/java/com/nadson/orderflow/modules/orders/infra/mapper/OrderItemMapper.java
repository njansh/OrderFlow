package com.nadson.orderflow.modules.orders.infra.mapper;

import com.nadson.orderflow.modules.orders.domain.OrderItem;
import com.nadson.orderflow.modules.orders.infra.persistence.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {
    public OrderItem toDomain(OrderItemEntity entity){
        if(entity == null){
            return null;
        }
return new OrderItem(
        entity.getProductId(),
        entity.getProductName(),
        entity.getQuantity(),
        entity.getPrice()
);
    }
  public OrderItemEntity toEntity(OrderItem domain){
        if(domain == null){
        return null;
    }
    return new OrderItemEntity(domain);
  }
}
