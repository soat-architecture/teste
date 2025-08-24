package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryMovementEntity;

public class InventoryMovementEntityMapper {

    private InventoryMovementEntityMapper() {}

    public static InventoryMovementEntity map(InventoryMovementDomain domain){
        return new InventoryMovementEntity(
                domain.getIdentifier(),
                domain.getPartId(),
                domain.getUserId(),
                domain.getServiceOrderId(),
                domain.getMovementType(),
                domain.getQuantityChanged(),
                domain.getQuantityBefore(),
                domain.getQuantityAfter(),
                domain.getReason(),
                domain.getCreatedAt()
        );
    }
}
