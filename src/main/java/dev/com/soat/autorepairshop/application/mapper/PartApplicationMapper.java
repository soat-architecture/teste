package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;

public class PartApplicationMapper {


    public static PartDomain toDomain(PartInputDTO dto) {
        return PartDomain.create(
                dto.name(),
                dto.sku(),
                dto.description(),
                dto.brand(),
                dto.sellingPrice(),
                dto.buyPrice()
        );
    }

    public static PartOutputDTO toDTO(final PartDomain domain) {
        return new PartOutputDTO(
          domain.getIdentifier(),
          domain.getName(),
          domain.getSku(),
          domain.getDescription(),
          domain.getBrand(),
          domain.getSellingPrice(),
          domain.getBuyPrice(),
          domain.getActive(),
          domain.getCreatedAt(),
          domain.getUpdatedAt()
        );
    }
}
