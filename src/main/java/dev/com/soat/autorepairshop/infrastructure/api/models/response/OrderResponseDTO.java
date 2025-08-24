package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Representa dados de uma OS.")
public record OrderResponseDTO(

        @Schema(description = "Identificador da OS.", example = "455432")
        @NotNull
        Long orderId,

        @Schema(description = "Documento do cliente (CPF/CNPJ).", example = "012.234.567-89")
        @NotBlank
        String clientDocument,

        @Schema(description = "Placa do veículo.", example = "ABC-1234 ou ABC1D34")
        @NotBlank
        String vehicleLicensePlate,

        @Schema(description = "Status da OS.", example = "Recebida")
        @NotBlank
        String status,

        @Schema(description = "Identificador do empregado.", example = "1465")
        @NotNull
        Long employeeIdentifier,

        @Schema(description = "Identificador do serviço.", example = "55687")
        @NotNull
        Long serviceId,

        @Schema(description = "Anotações da OS.", example = "Trocar parafuso.")
        @NotBlank
        String notes,

        @Schema(description = "Data de criação do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime updatedAt,

        @Schema(description = "Data de finalização da OS.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime completedAt
) {
}
