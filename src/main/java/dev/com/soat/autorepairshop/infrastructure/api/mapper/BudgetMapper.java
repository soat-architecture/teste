package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetDetailOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetDetailResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetItemResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetResponseDTO;

public final class BudgetMapper {

    private BudgetMapper() {}

    public static BudgetInputDTO mapToNewBudget(BudgetRequestDTO dto, Long employeeId) {
        if (dto == null) return null;
        return new BudgetInputDTO(
                null,
                dto.serviceOrderId(),
                employeeId,
                dto.notes(),
                dto.items()
        );
    }

    public static BudgetInputDTO map(BudgetRequestDTO body, Long employeeId, Long budgetId) {
        return new BudgetInputDTO(
                budgetId,
                body.serviceOrderId(),
                employeeId,
                body.notes(),
                body.items()
        );
    }

    public static BudgetDetailResponseDTO mapToResponseDTO(final BudgetDetailOutputDTO detail) {
        final var items = detail.items().stream().map(item -> new BudgetItemResponseDTO(
                item.identifier(),
                item.itemType(),
                item.serviceId(),
                item.partId(),
                item.quantity(),
                item.unitPrice()
        )).toList();

        final var budget = new BudgetResponseDTO(
                detail.budget().identifier(),
                detail.budget().serviceOrderId(),
                detail.budget().version(),
                detail.budget().totalAmount(),
                detail.budget().status(),
                detail.budget().notes(),
                detail.budget().createdAt(),
                detail.budget().evaluatedAt()
        );

        return new BudgetDetailResponseDTO(
                budget,
                items
        );
    }
}
