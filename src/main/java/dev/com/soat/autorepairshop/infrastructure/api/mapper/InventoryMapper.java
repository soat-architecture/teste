package dev.com.soat.autorepairshop.infrastructure.api.mapper;


import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;

import java.time.LocalDateTime;

public class InventoryMapper {

    private InventoryMapper() {}

    public static InventoryDomain map(final InventoryInputDTO input, final PartDomain part) {
        return new InventoryDomain(
                input.partId(),
                input.quantityChanged(),
                part.getCreatedAt(),
                LocalDateTime.now()
        );
    }

    public static InventoryDomain map(InventoryEntity inventory) {
        return new InventoryDomain(
            inventory.getPartId(),
            inventory.getQuantityOnHand(),
            inventory.getCreatedAt(),
            inventory.getUpdatedAt()
        );
    }
}
