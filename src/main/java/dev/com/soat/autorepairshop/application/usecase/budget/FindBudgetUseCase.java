package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.mapper.BudgetApplicationMapper;
import dev.com.soat.autorepairshop.application.mapper.BudgetItemApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.BudgetDetailOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindBudgetUseCase {

    private final OrderGateway orderGateway;
    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;

    public BudgetDetailOutputDTO execute(Long serviceOrderId){
        log.info("c=FindBudgetUseCase m=execute s=Start serviceOrderId={}", serviceOrderId);
        final OrderDomain order = orderGateway.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        final List<BudgetDomain> budgets = budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(order.getIdentifier());
        final BudgetDomain budget = budgets.stream().findFirst()
                .orElseThrow(() -> new NotFoundException("budget.not.found"));
        final List<BudgetItemDomain> budgetItems = budgetItemGateway.findByBudgetId(budget.getIdentifier());
        log.info("c=FindBudgetUseCase m=execute s=Done serviceOrderId={}", serviceOrderId);
        return new BudgetDetailOutputDTO(
                BudgetApplicationMapper.toOutputDTO(budget),
                budgetItems.stream().map(BudgetItemApplicationMapper::toOutputDTO).toList()
        );
    }

}
