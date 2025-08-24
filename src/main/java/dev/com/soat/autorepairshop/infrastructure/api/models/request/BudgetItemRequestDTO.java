package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;

public record BudgetItemRequestDTO(
    Long id,
    ItemTypeEnum type,
    Double quantity
) {
}
