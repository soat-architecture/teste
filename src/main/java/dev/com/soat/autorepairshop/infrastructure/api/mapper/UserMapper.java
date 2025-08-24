package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.UserInputDTO;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UserRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.UserResponseDTO;

public class UserMapper {
    private UserMapper() {

    }
    public static UserInputDTO map(
            final UserRequestDTO dto,
            final String password
    ) {
        return new UserInputDTO(
                dto.name(),
                dto.document(),
                dto.email(),
                password,
                dto.contractedAt(),
                dto.role()
        );
    }

    public static UserResponseDTO map(final UserOutputDTO dto) {
        return new UserResponseDTO(
                dto.identifier(),
                dto.document(),
                dto.name(),
                dto.email(),
                dto.contractedAt(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.role(),
                dto.status()
        );
    }
}
