package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class BudgetApplicationMapper {

    private BudgetApplicationMapper() {

    }

    public static BudgetDomain toOutputDTO(BudgetInputDTO dto, BigDecimal totalAmount) {
        return BudgetDomain.create(
                dto.serviceOrderId(),
                totalAmount,
                dto.notes()
        );
    }

    public static BudgetOutputDTO toOutputDTO(final BudgetDomain domain) {
        return new BudgetOutputDTO(
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

    public static BudgetDomain toDomainForCreate(BudgetInputDTO dto,
                                                 BigDecimal totalAmount,
                                                 int version,
                                                 LocalDateTime createdAt) {
        return BudgetDomain.createWithVersion(
                dto.serviceOrderId(),
                totalAmount,
                dto.notes(),
                version,
                createdAt
        );
    }

    public static BudgetDomain toDomainForNewVersionFrom(BudgetDomain base,
                                                         BudgetInputDTO dto,
                                                         BigDecimal totalAmount,
                                                         int newVersion) {
        return BudgetDomain.newVersionFrom(
                base,
                totalAmount,
                dto.notes(),
                newVersion
        );
    }
}
