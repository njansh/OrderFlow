package com.nadson.orderflow.modules.orders.infra.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateOrderStatusRequest(
        @NotBlank(message = "Status cannot be blank")
        String status
) {
}