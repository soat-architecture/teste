package dev.com.soat.autorepairshop.domain.gateway;


import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;

import java.util.List;

public interface BudgetItemGateway {

    List<BudgetItemDomain> saveAll(final List<BudgetItemDomain> items);

    List<BudgetItemDomain> findByBudgetId(Long identifier);

    List<BudgetItemDomain> findBudgetItemsByBudgetId(Long budgetDomainIdentifier);
}

