package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ServiceEntity;

public class ServiceEntityMapper {

    private ServiceEntityMapper() {
    }

    public static ServiceEntity toEntity(Long identifier, ServiceDomain domain) {
        ServiceEntity entity = new ServiceEntity(
                identifier,
                domain.getName(),
                domain.getDescription(),
                domain.getBasePrice());

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static ServiceEntity toEntity(ServiceDomain domain) {
        ServiceEntity entity = new ServiceEntity(
                domain.getIdentifier(),
                domain.getName(),
                domain.getDescription(),
                domain.getBasePrice());

        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    public static ServiceDomain toDomain(ServiceEntity entity){
        return new ServiceDomain(entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getBasePrice(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
