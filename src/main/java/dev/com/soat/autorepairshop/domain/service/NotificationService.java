package dev.com.soat.autorepairshop.domain.service;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;

public interface NotificationService {
    void sendBudgetNotification(final BudgetAggregateRoot budgetAggregateRoot);
}
