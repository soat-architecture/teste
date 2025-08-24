package dev.com.soat.autorepairshop.application.models.output;

import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;

import java.math.BigDecimal;

public record BudgetItemOutputDTO(
        Long identifier,
        ItemTypeEnum itemType,
        Long serviceId,
        Long partId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
