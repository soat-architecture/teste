package dev.com.soat.autorepairshop.application.models.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceOutputDTO(
        Long identifier,
        String name,
        String description,
        BigDecimal basePrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
