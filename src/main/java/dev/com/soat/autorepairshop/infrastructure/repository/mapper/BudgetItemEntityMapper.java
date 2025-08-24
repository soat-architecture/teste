package dev.com.soat.autorepairshop.infrastructure.repository.mapper;


import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetItemEntity;

public class BudgetItemEntityMapper {

    private BudgetItemEntityMapper(){}

    public static BudgetItemDomain toDomain(final BudgetItemEntity entity) {
        return BudgetItemDomain.restore(
                entity.getId(),
                entity.getBudgetId(),
                ItemTypeEnum.fromName(entity.getItemType().getName()),
                entity.getServiceId(),
                entity.getPartId(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getCreatedAt()
        );
    }

    public static BudgetItemEntity toEntity(final BudgetItemDomain domain) {
        return new BudgetItemEntity(
                domain.getIdentifier(),
                domain.getBudgetId(),
                domain.getItemType(),
                domain.getServiceId(),
                domain.getPartId(),
                domain.getQuantity(),
                domain.getUnitPrice(),
                domain.getCreatedAt()
        );
    }
}