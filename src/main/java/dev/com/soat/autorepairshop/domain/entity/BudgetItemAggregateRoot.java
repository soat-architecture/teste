package dev.com.soat.autorepairshop.domain.entity;

import lombok.Getter;

@Getter
public class BudgetItemAggregateRoot extends AggregateRoot<Long> {
    private final BudgetItemDomain budgetItem;
    private final ServiceDomain service;
    private final PartDomain part;

    private BudgetItemAggregateRoot(final ServiceDomain service,
                                    final PartDomain part,
                                    final BudgetItemDomain budgetItem) {
        this.service = service;
        this.part = part;
        this.budgetItem = budgetItem;
    }

    public static BudgetItemAggregateRoot create(final ServiceDomain service,
                                                 final PartDomain part,
                                                 final BudgetItemDomain budgetItem) {

        return new BudgetItemAggregateRoot(
                service,
                part,
                budgetItem
        );
    }
}
