package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponseDTO(
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
