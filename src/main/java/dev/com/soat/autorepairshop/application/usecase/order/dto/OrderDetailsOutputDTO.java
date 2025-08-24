package dev.com.soat.autorepairshop.application.usecase.order.dto;

import dev.com.soat.autorepairshop.domain.entity.*;

import java.util.List;

public record OrderDetailsOutputDTO(
        OrderDomain order,
        ClientDomain client,
        UserDomain employee,
        VehicleDomain vehicle,
        BudgetDomain budget,
        List<BudgetItemDomain> budgetItems,
        List<OrderHistoryDomain> history) {
}
