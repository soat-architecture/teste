package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa dados para atualização de um veículo.")
public record UpdateVehicleRequestDTO(

        @Schema(description = "Nova placa do veículo.", example = "ABC-1234 ou ABC1D34")
        String newLicensePlate,

        @Schema(description = "Documento do novo dono do veículo (CPF/CNPJ).", example = "012.234.567-89")
        String newDocument,

        @Schema(description = "Nova cor do veículo.", example = "Preto")
        String newColor,

        @Schema(description = "Ativa um veículo desativado no sistema.", example = "true")
        boolean active
) {
}
