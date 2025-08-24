package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryRequestDTO;

import java.time.LocalDateTime;

public class InventoryApplicationMapper {

    private InventoryApplicationMapper(){}

    public static InventoryDomain mapToDomain(final Long partId){
        final int defaultQuantityOnHand = 0;
        return new InventoryDomain(
                partId,
                defaultQuantityOnHand,
                LocalDateTime.now(),
                null
        );
    }

    public static InventoryInputDTO map(final InventoryRequestDTO inventory){
        return new InventoryInputDTO(inventory.partId(), inventory.quantityOnHand(), MovementType.INITIAL);
    }

    public static InventoryOutputDTO map(final Long partId, final InventoryDomain inventoryDomain){
        if(inventoryDomain == null){
            return mapNotOnHand(partId);
        }
        return new InventoryOutputDTO(inventoryDomain.getIdentifier(), inventoryDomain.getQuantityOnHand());
    }

    public static InventoryRequestDTO map(final InventoryOutputDTO inventory){
        return new InventoryRequestDTO(inventory.partId(), inventory.quantityOnHand());
    }

    public static InventoryOutputDTO mapNotOnHand(final Long identifier){
        return new InventoryOutputDTO(identifier, 0);
    }
}
