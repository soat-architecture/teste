package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;

public class OrderEntityMapper {

    private OrderEntityMapper() {

    }

    public static OrderDomain toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        if (entity.getBudget() == null){
            return OrderDomain.restore(
                    entity.getIdentifier(),
                    entity.getClient().getDocument(),
                    entity.getVehicle().getLicensePlate(),
                    entity.getStatus(),
                    entity.getNotes(),
                    entity.getEmployeeId(),
                    null,
                    entity.getServiceId(),
                    entity.getCreatedAt(),
                    entity.getUpdatedAt(),
                    entity.getCompletedAt()
            );
        }

        return OrderDomain.restore(
                entity.getIdentifier(),
                entity.getClient().getDocument(),
                entity.getVehicle().getLicensePlate(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getEmployeeId(),
                entity.getBudget().getBudgetId(),
                entity.getServiceId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCompletedAt()
        );
    }

    public static OrderEntity toEntity(OrderDomain domain) {
        if (domain == null) {
            return null;
        }

        var client = new ClientEntity();
        client.setDocument(domain.getClientDocument().unformat());

        var vehicle = new VehicleEntity();
        vehicle.setLicensePlate(domain.getVehicleLicensePlate().getValue());

        OrderEntity order = new OrderEntity();
        order.setIdentifier(domain.getIdentifier());
        order.setClient(client);
        order.setVehicle(vehicle);
        order.setStatus(domain.getStatus());
        order.setNotes(domain.getNotes());
        order.setEmployeeId(domain.getEmployeeId());
        order.setServiceId(domain.getServiceId());
        order.setCreatedAt(domain.getCreatedAt());
        order.setUpdatedAt(domain.getUpdatedAt());
        order.setCompletedAt(domain.getCompletedAt());

        return order;
    }

}
