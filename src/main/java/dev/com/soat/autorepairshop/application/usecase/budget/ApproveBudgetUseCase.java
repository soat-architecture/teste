package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApproveBudgetUseCase {

    private final BudgetGateway budgetGateway;
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;

    public void execute(Long serviceOrderId){
        log.info("c=ApproveBudgetUseCase m=execute s=start budgetId={}", serviceOrderId);
        final OrderDomain order = orderGateway.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        final BudgetDomain budget = budgetGateway.findById(order.getActiveBudgetId());
        if (budget == null) {
            throw new NotFoundException("budget.not.found");
        }

        budget.approved();
        budgetGateway.save(budget);

        order.approval();
        orderGateway.save(order);

        final OrderHistoryDomain orderHistoryDomain = ApplicationOrderHistoryMapper.map(order);
        orderHistoryGateway.save(orderHistoryDomain);
        log.info("c=CreateBudgetUseCase m=execute s=success budgetId={} serviceOrderId={}", serviceOrderId, budget.getServiceOrderId());
    }

}
