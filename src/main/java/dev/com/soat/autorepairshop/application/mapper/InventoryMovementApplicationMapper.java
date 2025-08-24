package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import lombok.val;

public final class InventoryMovementApplicationMapper {

    private InventoryMovementApplicationMapper() {}

    // Já existente
    public static InventoryMovementOutputDTO map(InventoryMovementDomain domain){
        return new InventoryMovementOutputDTO(
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

    public static InventoryMovementDomain newMovement(
            Long partId,
            Long userId,
            Long serviceOrderId,
            MovementType type,
            int quantityChanged,
            int quantityBefore,
            int quantityAfter,
            String reason
    ) {
        return InventoryMovementDomain.builder()
                .partId(partId)
                .userId(userId)
                .serviceOrderId(serviceOrderId)
                .movementType(type)
                .quantityChanged(quantityChanged)
                .quantityBefore(quantityBefore)
                .quantityAfter(quantityAfter)
                .reason(reason)
                .build();
    }

    public static InventoryMovementDomain outboundSale(
            Long partId,
            Long userId,
            Long serviceOrderId,
            int qtyConsumed,
            int quantityBefore,
            String reason
    ) {
        int quantityAfter = quantityBefore - qtyConsumed;
        return new InventoryMovementDomain(
                null,
                partId,
                userId,
                serviceOrderId,
                MovementType.OUTBOUND_SALE,
                -qtyConsumed,
                quantityBefore,
                quantityAfter,
                reason,
                null
        );
    }

    public static InventoryMovementDomain adjustmentIncrease(
            Long partId,
            Long userId,
            Long serviceOrderId,
            int qty,
            int before,
            String reason
    ) {
        return new InventoryMovementDomain(
                null,
                partId,
                userId,
                serviceOrderId,
                MovementType.ADJUSTMENT,
                qty,            // entrada -> positivo
                before,
                before + qty,
                reason,
                null
        );
    }
}
