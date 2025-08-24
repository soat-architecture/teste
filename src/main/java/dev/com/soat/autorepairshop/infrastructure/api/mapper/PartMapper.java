package dev.com.soat.autorepairshop.infrastructure.api.mapper;


import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.PartRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.PartResponseDTO;

public class PartMapper {

    public static PartInputDTO map(final PartRequestDTO dto){
        return new PartInputDTO(
                dto.name(),
                dto.sku(),
                dto.description(),
                dto.brand(),
                dto.sellingPrice(),
                dto.buyPrice()
        );
    }

    public static PartResponseDTO map(final PartOutputDTO dto) {
        return new PartResponseDTO(
                dto.identifier(),
                dto.name(),
                dto.sku(),
                dto.description(),
                dto.brand(),
                dto.sellingPrice(),
                dto.buyPrice(),
                dto.active(),
                dto.createdAt(),
                dto.updatedAt()
        );
    }
}

