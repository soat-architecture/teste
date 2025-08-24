package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;

import java.util.List;

public interface BudgetGateway {
    BudgetDomain save(final BudgetDomain budget);
    BudgetDomain findById(final long id);
    long countByServiceOrderId(long serviceOrderId);
    List<BudgetDomain> findAllByServiceOrderIdOrderByVersionDesc(long soId);
}
