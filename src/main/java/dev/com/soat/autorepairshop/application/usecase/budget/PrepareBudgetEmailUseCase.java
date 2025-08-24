package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PrepareBudgetEmailUseCase {

    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;
    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;
    private final ClientValidationUtils clientValidationUtils;

    public BudgetAggregateRoot execute(Long serviceOrderId) {
        log.info("c=PrepareBudgetEmailUseCase m=execute s=start serviceOrderId={}", serviceOrderId);
        final List<BudgetItemAggregateRoot> budgetItemsAggregateRoot = new ArrayList<>();

        final var budgets = budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(serviceOrderId);
        if (budgets.isEmpty())
            throw new NotFoundException("budget.not.found");
        final var serviceOrder = orderGateway.findById(serviceOrderId)
                .orElseThrow(() -> new NotFoundException("order.not.found"));
        final var client = clientValidationUtils.validateClientExistenceByDocument(serviceOrder.getClientDocument().getValue());
        final var budget = budgets.getFirst();
        final var budgetItems = budgetItemGateway.findBudgetItemsByBudgetId(budget.getIdentifier());

        budgetItems.forEach(item -> {
            if (item.getItemType() == ItemTypeEnum.SERVICE){
                final var service = serviceGateway.findById(item.getServiceId());
                final var budgetItemAggregate = BudgetItemAggregateRoot.create(
                        service,
                        null,
                        item
                );
                budgetItemsAggregateRoot.add(budgetItemAggregate);
            } else {
                final var part = partGateway.findById(item.getPartId());
                final var budgetItemAggregate = BudgetItemAggregateRoot.create(
                        null,
                        part,
                        item
                );
                budgetItemsAggregateRoot.add(budgetItemAggregate);
            }
        });

        serviceOrder.awaitingApproval();
        orderGateway.save(serviceOrder);
        final OrderHistoryDomain orderHistoryDomain = ApplicationOrderHistoryMapper.map(serviceOrder);
        orderHistoryGateway.save(orderHistoryDomain);
        log.info("c=PrepareBudgetEmailUseCase m=execute s=success serviceOrderId={} budgetId={} clientEmail={}", serviceOrder.getIdentifier(), budget.getIdentifier(), client.getEmail());
        return BudgetAggregateRoot.create(
                client,
                budget,
                budgetItemsAggregateRoot
        );
    }

}
