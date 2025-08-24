package dev.com.soat.autorepairshop.application.usecase.inventory.movements;

import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateInventoryMovementUseCase {
    private final InventoryMovementGateway gateway;

    public void execute(InventoryMovementInputDTO movementDTO){
        log.info("c=CreateInventoryMovementUseCase m=execute s=start");
        final InventoryMovementDomain domain = InventoryMovementMapper.map(movementDTO);
        gateway.save(domain);
    }
}
