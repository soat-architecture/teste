package dev.com.soat.autorepairshop.application.models.input;

import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;

import java.util.List;

public record BudgetInputDTO(
        Long budgetId,
        Long serviceOrderId,
        Long employeeId,
        String notes,
        List<BudgetItemRequestDTO> items
) {
}