package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Representa todos os veículos de alguém/alguma empresa.")
public record VehiclesOwnerResponseDTO(

        @Schema(description = "Documento do dono do veículo (CPF/CNPJ).", example = "012.234.567-89")
        String document,

        @Schema(description = "Lista de veículos do dono.")
        List<VehicleResponseDTO> vehicles
) {
}
