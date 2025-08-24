package dev.com.soat.autorepairshop.application.models.output;

import java.util.List;

public record BudgetDetailOutputDTO(
        BudgetOutputDTO budget,
        List<BudgetItemOutputDTO> items
) {
}
