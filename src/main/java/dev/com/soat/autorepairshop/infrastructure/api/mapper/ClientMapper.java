package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;

public class ClientMapper {

    private ClientMapper() {}

    public static ClientDomain map(ClientInputDTO client) {
        if (client == null) {
            return null;
        }
        return new ClientDomain(
            client.identifier(),
            client.name(),
            client.document(),
            client.phone(),
            client.email(),
            client.status(),
            null,
            null
        );
    }

    public static ClientDomain map(ClientEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ClientDomain(
            entity.getId(),
            entity.getName(),
            entity.getDocument(),
            entity.getPhone(),
            entity.getEmail(),
            entity.getStatus(),
            entity.getCreatedAt(),
            null
        );
    }
}
