package dev.com.soat.autorepairshop.infrastructure.exception.models;

public record ExceptionDTO(
        String error,
        String message,
        String path
) {
}
