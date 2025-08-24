package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.BudgetRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.ClientRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.OrderRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.VehicleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.OrderEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderGatewayImpl implements OrderGateway {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final VehicleRepository vehicleRepository;
    private final BudgetRepository budgetRepository;

    @Override
    public OrderDomain save(OrderDomain orderDomain) {
        final OrderEntity entity = OrderEntityMapper.toEntity(orderDomain);

        final var client = clientRepository.findByDocument(orderDomain.getClientDocument().unformat());
        final var vehicle = vehicleRepository.findVehicleByLicensePlate(orderDomain.getVehicleLicensePlate().getValue());

        if (orderDomain.getActiveBudgetId() != null){
            final var budget = budgetRepository.findById(orderDomain.getActiveBudgetId()).orElse(null);
            entity.setBudget(budget);
        }

        entity.setClient(client);
        entity.setVehicle(vehicle);

        final OrderEntity saved = orderRepository.save(entity);
        return OrderEntityMapper.toDomain(saved);
    }

    @Override
    public OrderDomain findActiveOrderByVehicleLicensePlate(String vehicleLicensePlate) {
        OrderEntity orderEntity = orderRepository.findOrderActiveByVehicleLicensePlate(vehicleLicensePlate);

        if (orderEntity != null) {
            return OrderEntityMapper.toDomain(orderEntity);
        }

        return null;
    }

    public List<OrderDomain> findAllOrders(){
        return orderRepository.findAll()
                .stream().map(OrderEntityMapper::toDomain).toList();
    }

    @Override
    public List<OrderDomain> findAllByStatus(List<OrderStatusType> status) {
        return orderRepository.findAllByStatus(status).stream()
                .map(OrderEntityMapper::toDomain).toList();
    }

    public Optional<OrderDomain> findById(Long id) {
        return orderRepository.findById(id).map(OrderEntityMapper::toDomain);
    }

    @Override
    public void setActiveBudget(Long orderId, Long budgetId) {
        int rows = orderRepository.updateActiveBudget(orderId, budgetId);
        if (rows == 0) {
            throw new IllegalStateException("Failed to set active budget: order not updated");
        }
    }
}
