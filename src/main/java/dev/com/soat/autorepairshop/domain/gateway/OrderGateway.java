package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;

import java.util.List;

import java.util.Optional;

public interface OrderGateway {

    OrderDomain save(OrderDomain order);

    OrderDomain findActiveOrderByVehicleLicensePlate(String vehicleLicensePlate);

    List<OrderDomain> findAllOrders();

    List<OrderDomain> findAllByStatus(List<OrderStatusType> status);

    Optional<OrderDomain> findById(Long id);

    void setActiveBudget(Long orderId, Long budgetId);
}
