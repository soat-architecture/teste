package dev.com.soat.autorepairshop.application.models.output;

import java.time.LocalDateTime;

public record UserOutputDTO(
        Long identifier,
        String name,
        String document,
        String email,
        String password,
        LocalDateTime contractedAt,
        String role,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
