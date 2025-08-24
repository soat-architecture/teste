package dev.com.soat.autorepairshop.infrastructure.exception.models;

import java.util.List;

public record ValidationErrorDTO(
        String message,
        List<ValidationFieldErrorDTO> fieldErrors,
        String path
) {

}