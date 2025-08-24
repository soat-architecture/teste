package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetEntity;

public class BudgetEntityMapper {

    private BudgetEntityMapper(){}

    public static BudgetDomain toDomain(final BudgetEntity dto){
        return BudgetDomain.restore(
                dto.getBudgetId(),
                dto.getServiceOrderId(),
                dto.getVersion(),
                dto.getTotalAmount(),
                BudgetStatus.fromName(dto.getStatus().getName()),
                dto.getNotes(),
                dto.getCreatedAt(),
                dto.getEvaluatedAt()
        );
    }

    public static BudgetEntity toEntity(BudgetDomain domain) {
        return new BudgetEntity(
                domain.getIdentifier(),
                domain.getServiceOrderId(),
                domain.getVersion(),
                domain.getTotalAmount(),
                domain.getStatus(),
                domain.getNotes(),
                domain.getCreatedAt(),
                domain.getEvaluatedAt()
        );
    }
}
