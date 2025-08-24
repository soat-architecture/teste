package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.BudgetRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetGatewayImpl implements BudgetGateway {

    private final BudgetRepository repository;

    @Override
    public BudgetDomain save(BudgetDomain budget) {
        final var entity = BudgetEntityMapper.toEntity(budget);
        final var saved = repository.save(entity);
        return BudgetEntityMapper.toDomain(saved);
    }

    @Override
    public BudgetDomain findById(long id) {
        final var entity = repository.findById(id).orElse(null);
        return entity != null ? BudgetEntityMapper.toDomain(entity) : null;
    }

    @Override
    public long countByServiceOrderId(long serviceOrderId) {
        return repository.countByServiceOrderId(serviceOrderId);
    }

    @Override
    public List<BudgetDomain> findAllByServiceOrderIdOrderByVersionDesc(long soId) {
        return repository.findByServiceOrderIdOrderByVersionDesc(soId)
                .stream().map(BudgetEntityMapper::toDomain).toList();
    }
}
