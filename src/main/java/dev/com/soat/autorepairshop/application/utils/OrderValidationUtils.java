package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderValidationUtils {

    private final OrderGateway orderGateway;

    public void validateActiveOrderByVehicleLicensePlate(String vehicleLicensePlate) {
        OrderDomain orderDomain = orderGateway.findActiveOrderByVehicleLicensePlate(vehicleLicensePlate);
        if (orderDomain != null) {
            throw new ConflictException("vehicle.has.active.order");
        }
    }

    public OrderDomain validateOrderIsAssignedToEmployee(final Long orderId, final Long employeeId){
        final var order = orderGateway.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        if (order.getEmployeeId() == null || !order.getEmployeeId().equals(employeeId)){
            throw new DomainException("order.not.assigned.to.employee");
        }

        return order;
    }
}
