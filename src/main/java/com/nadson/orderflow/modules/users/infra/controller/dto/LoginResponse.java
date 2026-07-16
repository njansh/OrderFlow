package com.nadson.orderflow.modules.users.infra.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "Token JWT de acesso. Use apenas este valor string (sem aspas ou chaves) no campo de Autorização do Swagger.")
        String token
) {
}
