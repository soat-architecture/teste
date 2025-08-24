package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;

import java.math.BigDecimal;

public record BudgetItemResponseDTO(
        Long identifier,
        ItemTypeEnum itemType,
        Long serviceId,
        Long partId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
