package dev.com.soat.autorepairshop.application.models.output;

import dev.com.soat.autorepairshop.domain.enums.MovementType;

import java.time.LocalDateTime;

public record InventoryMovementOutputDTO(
        Long identifier,
        Long partId,
        Long userId,
        Long serviceOrderId,
        MovementType movementType,
        Integer quantityChanged,
        Integer quantityBefore,
        Integer quantityAfter,
        String reason,
        LocalDateTime createdAt
) {
}
