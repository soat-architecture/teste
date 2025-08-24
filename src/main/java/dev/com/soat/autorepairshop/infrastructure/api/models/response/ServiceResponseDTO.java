package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ServiceResponseDTO(

        @Schema(description = "Identificador único de serviço")
        Long identifier,

        @Schema(description = "Nome do serviço", example = "Troca de óleo")
        String name,

        @Schema(description = "Descrição do serviço")
        String description,

        @Schema(description = "Valor base do serviço", example = "10.00")
        BigDecimal basePrice,

        @Schema(description = "Data de criação do registro")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização do registro")
        LocalDateTime updatedAt
) {
}
