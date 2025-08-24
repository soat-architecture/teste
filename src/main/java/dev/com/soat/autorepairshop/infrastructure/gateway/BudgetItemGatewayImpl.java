package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.BudgetItemRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetItemEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetItemEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetItemGatewayImpl implements BudgetItemGateway {

    private final BudgetItemRepository repository;


    @Override
    public List<BudgetItemDomain> saveAll(List<BudgetItemDomain> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }

        var entities = items.stream()
                .map(BudgetItemEntityMapper::toEntity)
                .toList();

        var savedEntities = repository.saveAll(entities);

        return savedEntities.stream()
                .map(BudgetItemEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<BudgetItemDomain> findByBudgetId(Long identifier) {
        if (identifier == null) {
            return List.of();
        }

        return repository.findAllByBudgetId(identifier).stream()
                .map(BudgetItemEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<BudgetItemDomain> findBudgetItemsByBudgetId(Long budgetDomainIdentifier) {
        List<BudgetItemEntity> entities = repository.findBudgetItemEntitiesByBudgetId(budgetDomainIdentifier);

        return entities.stream().map(BudgetItemEntityMapper::toDomain)
                .toList();
    }
}
