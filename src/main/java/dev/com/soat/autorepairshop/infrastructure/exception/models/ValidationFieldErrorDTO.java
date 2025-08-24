package dev.com.soat.autorepairshop.infrastructure.exception.models;

public record ValidationFieldErrorDTO(
        String field,
        String rejectedValue,
        String message
) {

}