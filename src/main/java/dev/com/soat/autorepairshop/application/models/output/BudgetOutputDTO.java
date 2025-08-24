package dev.com.soat.autorepairshop.application.models.output;

import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetOutputDTO(
        Long identifier,
        Long serviceOrderId,
        Integer version,
        BigDecimal totalAmount,
        BudgetStatus status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime evaluatedAt
) {
}
