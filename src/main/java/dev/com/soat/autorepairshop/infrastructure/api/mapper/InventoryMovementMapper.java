package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryUpdateRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryMovementEntity;


public class InventoryMovementMapper {

    private InventoryMovementMapper() {}

    public static InventoryInputDTO map(final Long partId, final InventoryUpdateRequestDTO request){
        return new InventoryInputDTO(
                partId,
                request.quantityChanged(),
                request.movementType()
        );
    }

    public static InventoryMovementDomain map(InventoryMovementEntity entity){
        return new InventoryMovementDomain(
                entity.getId(),
                entity.getPartId(),
                entity.getUserId(),
                entity.getServiceOrderId(),
                entity.getMovementType(),
                entity.getQuantityChanged(),
                entity.getQuantityBefore(),
                entity.getQuantityAfter(),
                entity.getReason(),
                entity.getCreatedAt()
        );
    }

    public static InventoryMovementDomain map(InventoryMovementInputDTO request){
        return new InventoryMovementDomain(
                null,
                request.partId(),
                request.userId(),
                request.serviceOrderId(),
                request.movementType(),
                request.quantityChanged(),
                request.quantityBefore(),
                request.quantityAfter(),
                request.reason(),
                null
        );
    }
}
