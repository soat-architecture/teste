package dev.com.soat.autorepairshop.application.usecase.inventory.movements;

import dev.com.soat.autorepairshop.application.mapper.InventoryMovementApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindInventoryMovementsUseCase {

    private final PartGateway partGateway;
    private final InventoryMovementGateway inventoryMovementGateway;

    public List<InventoryMovementOutputDTO> execute(String partSku){
        PartDomain part = partGateway.findBySku(partSku);

        if(part == null){
            throw new NotFoundException("Part not found.");
        }

        List<InventoryMovementDomain> result = inventoryMovementGateway.findByPartId(part.getIdentifier());
        return result.stream()
                .map(InventoryMovementApplicationMapper::map)
                .toList();
    }
}