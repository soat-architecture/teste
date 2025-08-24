package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.order.dto.OrderDetailsOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.OrderRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.OrderDetailsResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.OrderResponseDTO;

public class OrderMapper {

    private OrderMapper() {

    }

    public static OrderInputDTO map(final OrderRequestDTO dto, final Long employeeId) {
        return new OrderInputDTO(
                dto.clientDocument(),
                dto.vehicleLicensePlate(),
                employeeId,
                dto.serviceId(),
                dto.notes()
        );
    }

    public static OrderResponseDTO map(OrderOutputDTO dto) {
        return new OrderResponseDTO(
                dto.orderId(),
                dto.clientDocument(),
                dto.vehicleLicensePlate(),
                dto.status(),
                dto.employeeIdentifier(),
                dto.serviceId(),
                dto.notes(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.completedAt()
        );
    }

    public static OrderDetailsResponseDTO map(OrderDetailsOutputDTO dto){
        return new OrderDetailsResponseDTO(
                dto.order().getIdentifier(),
                dto.client().getFormatedDocument(),
                dto.vehicle().getLicensePlate().toString(),
                dto.order().getStatus(),
                dto.order().getNotes(),
                dto.employee().getName(),
                dto.order().getCreatedAt(),
                dto.order().getUpdatedAt(),
                dto.order().getCompletedAt(),
                dto.budget(),
                dto.budgetItems(),
                dto.history()
        );
    }

}
