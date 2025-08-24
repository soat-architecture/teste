package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;

public class InventoryEntityMapper {

    private InventoryEntityMapper() {}

    public static InventoryEntity map(InventoryDomain domain){
        return new InventoryEntity(
                domain.getIdentifier(),
                domain.getQuantityOnHand(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
