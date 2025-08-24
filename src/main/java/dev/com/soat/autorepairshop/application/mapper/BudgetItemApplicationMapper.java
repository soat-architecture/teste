package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.output.BudgetItemOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;

import java.math.BigDecimal;

public class BudgetItemApplicationMapper {

    private BudgetItemApplicationMapper() {}

    public static BudgetItemDomain toDomain(BudgetItemRequestDTO dto, Long budgetId, BigDecimal unitPrice) {
        return BudgetItemDomain.create(
                budgetId,
                dto.type(),
                dto.id(),
                dto.quantity().intValue(),
                unitPrice
        );
    }
    public static BudgetItemOutputDTO toOutputDTO(final BudgetItemDomain domain) {
        return new BudgetItemOutputDTO(
                domain.getIdentifier(),
                domain.getItemType(),
                domain.getServiceId(),
                domain.getPartId(),
                domain.getQuantity(),
                domain.getUnitPrice()
        );
    }
}
