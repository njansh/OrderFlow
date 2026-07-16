package com.nadson.orderflow.modules.products.infra.controller.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id, String name, BigDecimal price) {
}
