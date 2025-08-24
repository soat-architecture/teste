package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;

public class ApplicationOrderMapper {

    private ApplicationOrderMapper() {

    }

    public static OrderDomain toDomain(OrderInputDTO dto) {
        return OrderDomain.create(
                dto.clientDocument(),
                dto.vehicleLicensePlate(),
                dto.notes(),
                dto.employeeIdentifier(),
                dto.serviceId()
        );
    }

    public static OrderOutputDTO toDTO(OrderDomain domain) {
        return new OrderOutputDTO(
                domain.getIdentifier(),
                domain.getClientDocument().getValue(),
                domain.getVehicleLicensePlate().getValue(),
                domain.getStatus().getDescription(),
                domain.getEmployeeId(),
                domain.getServiceId(),
                domain.getNotes(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getCompletedAt()
        );
    }
}
