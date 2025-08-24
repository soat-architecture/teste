package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Representa dados de um funcionário.")
public record UserResponseDTO(
        @Schema(description = "Identificador do funcionário.", example = "1")
        Long identifier,
        @Schema(description = "Documento do funcionário.", example = "012.234.567-89")
        String document,
        @Schema(description = "Nome do funcionário.", example = "John Doe")
        String name,
        @Schema(description = "E-mail do funcionário.", example = "john.doe@example.com")
        String email,
        @Schema(description = "Data de contratação do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime contractedAt,
        @Schema(description = "Data de criação do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime createdAt,
        @Schema(description = "Data de atualização do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime updatedAt,
        @Schema(description = "Permissão do funcionário.", example = "ADMIN")
        String role,
        @Schema(description = "Status atual do funcionário.", example = "ACTIVE")
        String status
) {
}
