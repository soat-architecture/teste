package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.usecase.order.dto.OrderDetailsOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.*;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindOrderDetailsUseCase {

    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;
    private final ClientGateway clientGateway;
    private final VehicleGateway vehicleGateway;
    private final BudgetGateway budgetGateway;
    private final BudgetItemGateway budgetItemGateway;
    private final UserGateway userGateway;

    public OrderDetailsOutputDTO execute(Long id){
        OrderDomain order = orderGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        ClientDomain client = clientGateway.findByDocument(order.getClientDocument().onlyNumbers());

        VehicleDomain vehicle = vehicleGateway.findVehicleByLicensePlate(order.getVehicleLicensePlate().getValue())
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));

        List<BudgetDomain> budgets = budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(order.getIdentifier());
        BudgetDomain budget = budgets.stream().findFirst().orElse(null);

        List<BudgetItemDomain> budgetItems = new ArrayList<>();
        if(budget != null){
            budgetItems = budgetItemGateway.findByBudgetId(budget.getIdentifier());
        }

        List<OrderHistoryDomain> history = orderHistoryGateway.findAllByOrderId(order.getIdentifier());

        UserDomain user = null;
        if(order.getEmployeeId() != null){
            user = userGateway.findEmployeeById(order.getEmployeeId())
                    .orElse(null);
        }

        return new OrderDetailsOutputDTO(
                order,
                client,
                user,
                vehicle,
                budget,
                budgetItems,
                history
        );
    }
}
