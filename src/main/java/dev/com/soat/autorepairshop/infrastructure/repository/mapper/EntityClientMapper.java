package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;

public class EntityClientMapper {

    private EntityClientMapper(){

    }

    public static ClientEntity map(Long id, ClientDomain domain) {
        if (domain == null) {
            return null;
        }
        ClientEntity entity = new ClientEntity(
            id,
            domain.getName(),
            domain.getUnformattedDocument(),
            domain.getPhone(),
            domain.getStatus(),
            domain.getEmail()
        );
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

}
