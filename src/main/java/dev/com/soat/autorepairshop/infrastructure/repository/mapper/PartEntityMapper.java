package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.PartEntity;

public class PartEntityMapper {

    private PartEntityMapper() {

    }

    public static PartDomain toDomain(final PartEntity dto) {
        return PartDomain.restore(
                dto.getPartId(),
                dto.getName(),
                dto.getSku(),
                dto.getDescription(),
                dto.getBrand(),
                dto.getSellingPrice(),
                dto.getBuyPrice(),
                dto.getActive(),
                dto.getCreatedAt(),
                dto.getUpdatedAt()
        );
    }

    public static PartEntity toEntity(final PartDomain domain) {
        return new PartEntity(
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
