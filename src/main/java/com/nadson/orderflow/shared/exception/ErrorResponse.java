package com.nadson.orderflow.shared.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int status,
        String error,
        String mensage,
         LocalDateTime timestamp

) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, LocalDateTime.now());
    }
}
