package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.CreateInventoryMovementUseCase;
import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateInventoryUseCase {
    private final InventoryGateway inventoryGateway;
    private final PartGateway partGateway;
    private final CreateInventoryMovementUseCase createInventoryMovementUseCase;

    @Transactional
    public void execute(final InventoryInputDTO inventory) {
        log.info("c=CreateInventoryUseCase m=execute s=start partId = {}", inventory.partId());

        final var part = partGateway.findById(inventory.partId());

        if(part == null) {
            throw new DomainException("part.not.found");
        }

        final InventoryDomain domain = InventoryMapper.map(inventory, part);
        final InventoryMovementInputDTO movementDTO = new InventoryMovementInputDTO(
                part.getIdentifier(),
                null,
                null,
                MovementType.INITIAL,
                inventory.quantityChanged(),
                0,
                inventory.quantityChanged(),
                "Initial inventory"
        );

        createInventoryMovementUseCase.execute(movementDTO);
        inventoryGateway.save(domain);
    }
}
