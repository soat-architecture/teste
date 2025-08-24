package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BudgetDetailResponseDTO(
        @Schema(description = "Orçamento atual vinculado a OS")
        BudgetResponseDTO budget,
        @Schema(description = "Descrição dos itens presentes no orçamento")
        List<BudgetItemResponseDTO> budgetItems
) {
}
