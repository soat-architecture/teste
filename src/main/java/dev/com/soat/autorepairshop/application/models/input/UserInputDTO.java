package dev.com.soat.autorepairshop.application.models.input;

import java.time.LocalDateTime;

public record UserInputDTO(
        String name,
        String document,
        String email,
        String password,
        LocalDateTime contractedAt,
        String role
) {
}
