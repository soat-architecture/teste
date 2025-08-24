package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ServiceRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ServiceResponseDTO;

import java.time.LocalDateTime;

public class ServiceMapper {

    private ServiceMapper() {}

    public static ServiceDomain map(ServiceInputDTO dto) {
        return new ServiceDomain(
                null,
                dto.name(),
                dto.description(),
                dto.basePrice(),
                LocalDateTime.now(),
                LocalDateTime.now()
                );
    }

    public static ServiceInputDTO map(ServiceRequestDTO request){
        return new ServiceInputDTO(
                request.name(),
                request.description(),
                request.basePrice()
        );
    }

    public static ServiceResponseDTO map(ServiceOutputDTO outputDTO){
        return new ServiceResponseDTO(
                outputDTO.identifier(),
                outputDTO.name(),
                outputDTO.description(),
                outputDTO.basePrice(),
                outputDTO.createdAt(),
                outputDTO.updatedAt()
        );
    }
}
