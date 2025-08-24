package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.gateway.PartInventoryGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.PartInventoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartInventoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PartInventoryGatewayImpl implements PartInventoryGateway {

    private final PartInventoryRepository repository;

    @Override
    public Integer getOnHand(Long partId) {
        return repository.findOnHand(partId);
    }

    @Override
    @Transactional
    public void decrease(Long partId, int quantity) {
        int rows = repository.tryDecrease(partId, quantity);
        if (rows == 0) {
            throw new IllegalStateException("insufficient.stock.partId=" + partId);
        }
    }

    @Override
    @Transactional
    public void increase(Long partId, int quantity) {
        int rows = repository.tryIncrease(partId, quantity);
        if (rows > 0) return;
        try {
            PartInventoryEntity created = new PartInventoryEntity();
            created.setPartId(partId);
            created.setQuantityOnHand(quantity);
            created.setCreatedAt(LocalDateTime.now());
            created.setUpdatedAt(LocalDateTime.now());
            repository.save(created);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            int retried = repository.tryIncrease(partId, quantity);
            if (retried == 0) {
                throw e;
            }
        }
    }
}
