package dev.com.soat.autorepairshop.domain.entity;

import lombok.Getter;

import java.util.List;

@Getter
public class BudgetAggregateRoot extends AggregateRoot<Long> {
    private final ClientDomain client;
    private final BudgetDomain budget;
    private final List<BudgetItemAggregateRoot> budgetItem;

    private BudgetAggregateRoot(final ClientDomain client,
                                final BudgetDomain budget,
                                final List<BudgetItemAggregateRoot> budgetItem) {
        this.client = client;
        this.budget = budget;
        this.budgetItem = budgetItem;
    }

    public static BudgetAggregateRoot create(final ClientDomain client,
                                             final BudgetDomain budget,
                                             final List<BudgetItemAggregateRoot> budgetItems) {

        return new BudgetAggregateRoot(
                client,
                budget,
                budgetItems
        );
    }
}
