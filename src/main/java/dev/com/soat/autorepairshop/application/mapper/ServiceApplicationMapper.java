package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;

public class ServiceApplicationMapper {
    private ServiceApplicationMapper(){}

    public static ServiceDomain toDomain(ServiceInputDTO dto){
        return ServiceDomain.create(
                dto.name(),
                dto.description(),
                dto.basePrice()
        );
    }

    public static ServiceOutputDTO toDTO(final ServiceDomain domain){
        return new ServiceOutputDTO(
                domain.getIdentifier(),
                domain.getName(),
                domain.getDescription(),
                domain.getBasePrice(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}