package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Slf4j
@RequiredArgsConstructor
public class NotApproveBudgetUseCase {

    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;
    private final PartInventoryConsumptionHelper partInventoryConsumptionHelper;

    @Transactional
    public void execute(Long serviceOrderId){
        log.info("c=NotApproveBudgetUseCase m=execute s=start serviceOrderId={}", serviceOrderId);
        final OrderDomain order = orderGateway.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        final List<BudgetDomain> budgets = budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(serviceOrderId);

        budgets.forEach(item -> {
            final BudgetDomain budget = budgetGateway.findById(item.getIdentifier());
            final List<BudgetItemDomain> items = budgetItemGateway.findBudgetItemsByBudgetId(item.getIdentifier());


            budget.notApproved();
            budgetGateway.save(budget);

            partInventoryConsumptionHelper.restoreFor(
                    items,
                    null,
                    order.getIdentifier(),
                    "BUDGET_NOT_APPROVED"
            );
        });

        order.notApproval();
        orderGateway.save(order);

        final OrderHistoryDomain orderHistoryDomain = ApplicationOrderHistoryMapper.map(order);
        orderHistoryGateway.save(orderHistoryDomain);
        log.info("c=NotApproveBudgetUseCase m=execute s=success serviceOrderId={}", serviceOrderId);
    }
}
