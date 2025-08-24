package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.domain.gateway.InventoryGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.InventoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.InventoryEntityMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InventoryGatewayImpl implements InventoryGateway {

    private final InventoryRepository repository;

    @Override
    public InventoryDomain save(InventoryDomain inventory) {
        InventoryEntity entity = InventoryEntityMapper.map(inventory);
        entity = repository.save(entity);
        return InventoryMapper.map(entity);
    }

    @Override
    public void saveNotReturns(InventoryDomain inventory) {
        InventoryEntity entity = InventoryEntityMapper.map(inventory);
        repository.insertInventory(
                entity.getPartId(),
                entity.getQuantityOnHand(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public InventoryDomain update(InventoryDomain existingInventory, InventoryDomain newData) {
        InventoryEntity inventory = InventoryEntityMapper.map(newData);
        inventory.setUpdatedAt(LocalDateTime.now());

        inventory = repository.save(inventory);
        return InventoryMapper.map(inventory);
    }

    @Override
    public InventoryDomain findById(Long partId) {
        InventoryEntity inventory = repository.findById(partId).orElse(null);

        if( inventory == null){
            return null;
        }else{
            return InventoryMapper.map(inventory);
        }
    }

    @Override
    public void delete(Long partId) {
        InventoryEntity inventory = repository.findById(partId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        repository.delete(inventory);
    }
}
