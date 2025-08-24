package dev.com.soat.autorepairshop.application.usecase.inventory;

import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindInventoryUseCase {

    private final InventoryGateway inventoryGateway;

    public InventoryOutputDTO execute(Long partId) {
        log.info("c=FindInventoryUseCase m=execute s=start partId = {}", partId);
        var inventory = inventoryGateway.findById(partId);
        return InventoryApplicationMapper.map(partId, inventory);
    }
}
