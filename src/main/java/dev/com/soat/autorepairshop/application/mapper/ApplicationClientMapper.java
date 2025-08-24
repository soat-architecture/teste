package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ClientRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ClientResponseDTO;

public class ApplicationClientMapper {

    private ApplicationClientMapper(){

    }

    public static ClientInputDTO map(final ClientRequestDTO request) {
        return new ClientInputDTO(
            null,
            request.name(),
            request.document(),
            request.phone(),
            request.email(),
            request.status() == null ? ClientStatus.ACTIVE : request.status()
        );
    }

    public static ClientInputDTO map(final ClientDomain domain) {
        if (domain == null) {
            return null;
        }
        return new ClientInputDTO(
            domain.getIdentifier(),
            domain.getName(),
            domain.getUnformattedDocument(),
            domain.getPhone(),
            domain.getEmail(),
            domain.getStatus()
        );
    }

    public static ClientResponseDTO map(final ClientInputDTO client) {
        return new ClientResponseDTO(
            client.identifier(),
            client.name(),
            client.document(),
            client.phone(),
            client.email(),
            client.status()
        );
    }

}