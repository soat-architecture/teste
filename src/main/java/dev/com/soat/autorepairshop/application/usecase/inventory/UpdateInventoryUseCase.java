package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.CreateInventoryMovementUseCase;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateInventoryUseCase {

    private final InventoryGateway gateway;
    private final CreateInventoryMovementUseCase createInventoryMovementUseCase;

    @Transactional
    public void execute(final Long partId, final InventoryInputDTO inventory){
        log.info("c=UpdateInventoryUseCase m=execute s=start partId = {}", partId);

        int resultingQuantity = 0;
        final var existingInventory = gateway.findById(partId);

        if(existingInventory == null){
            throw new DomainException("Inventory not found");
        }

        if(inventory.movementType() == MovementType.INBOUND){
            resultingQuantity = existingInventory.getQuantityOnHand() + inventory.quantityChanged();
        } else if (inventory.movementType() == MovementType.OUTBOUND_SALE){
            resultingQuantity = existingInventory.getQuantityOnHand() - inventory.quantityChanged();
        }

        if(resultingQuantity < 0){
            throw new DomainException("Inventory quantity can't be lesser than zero");
        }

        final InventoryDomain newData = new InventoryDomain(existingInventory.getIdentifier(),
                                                            resultingQuantity,
                                                            existingInventory.getCreatedAt(),
                                                            LocalDateTime.now());

        final InventoryMovementInputDTO movementDTO = new InventoryMovementInputDTO(
                newData.getIdentifier(),
                null,
                null,
                inventory.movementType(),
                inventory.quantityChanged(),
                existingInventory.getQuantityOnHand(),
                resultingQuantity,
                inventory.movementType().getName()
        );

        createInventoryMovementUseCase.execute(movementDTO);
        gateway.update(existingInventory, newData);
    }
}