package com.nadson.orderflow.modules.orders.domain;

public enum OrderStatus {
    PENDING,
    PREPARING,
    OUT_FOR_DELIVERY,
    DELIVERED,
    COMPLETED,
    CANCELED
}