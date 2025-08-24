package dev.com.soat.autorepairshop.application.models.input;

import dev.com.soat.autorepairshop.domain.enums.MovementType;

public record InventoryInputDTO(
        Long partId,
        Integer quantityChanged,
        MovementType movementType
) {
}
