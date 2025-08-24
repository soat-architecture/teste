package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.InventoryMovementRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.InventoryMovementEntityMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryMovementEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InventoryMovementGatewayImpl implements InventoryMovementGateway {

    private final InventoryMovementRepository repository;

    @Override
    @Transactional
    public void save(InventoryMovementDomain domain) {
        InventoryMovementEntity entity = InventoryMovementEntityMapper.map(domain);
        repository.save(entity);
    }

    @Override
    public List<InventoryMovementDomain> findByPartId(Long partId) {
        return Optional.ofNullable(repository.findAllByPartId(partId))
                .orElse(Collections.emptyList())
                .stream()
                .map(InventoryMovementMapper::map)
                .toList();
    }
}
