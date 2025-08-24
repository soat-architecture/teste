package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Representa dados para a criação de uma OS.")
public record OrderRequestDTO(

        @Schema(description = "Documento do cliente (CPF/CNPJ).", example = "012.234.567-89")
        @NotBlank
        String clientDocument,

        @Schema(description = "Placa do veículo.", example = "ABC-1234 ou ABC1D34")
        @NotBlank
        String vehicleLicensePlate,

        @Schema(description = "Identificador do serviço.", example = "55687")
        @NotNull
        Long serviceId,

        @Schema(description = "Anotações da OS.", example = "Trocar parafuso.")
        @NotBlank
        String notes
) {}