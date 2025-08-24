package dev.com.soat.autorepairshop.application.models.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PartOutputDTO(
        Long identifier,
        String name,
        String sku,
        String description,
        String brand,
        BigDecimal sellingPrice,
        BigDecimal buyPrice,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}