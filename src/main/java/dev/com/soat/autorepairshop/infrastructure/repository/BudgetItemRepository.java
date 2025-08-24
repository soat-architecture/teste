package dev.com.soat.autorepairshop.infrastructure.repository;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItemEntity, Long> {
    List<BudgetItemEntity> findAllByBudgetId(Long budgetId);
    List<BudgetItemEntity> findBudgetItemEntitiesByBudgetId(Long budgetId);
}

