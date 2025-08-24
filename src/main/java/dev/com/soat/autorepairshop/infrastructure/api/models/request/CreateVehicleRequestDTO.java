package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Representa dados para o cadastro de um veículo.")
public record CreateVehicleRequestDTO(

        @Schema(description = "Placa do veículo.", example = "ABC-1234 ou ABC1D34")
        @NotBlank
        String licensePlate,

        @Schema(description = "Marca do veículo.", example = "Ford")
        @NotBlank
        String brand,

        @Schema(description = "Modelo do veículo.", example = "Focus")
        @NotBlank
        String model,

        @Schema(description = "Ano de fabricação do veículo.", example = "2018")
        @NotNull
        Integer manufactureYear,

        @Schema(description = "Tipo do veículo", example = "Carro ou Moto")
        @NotBlank
        String vehicleType,

        @Schema(description = "Tipo da carroceria caso o veículo seja um carro", example = "Hatch")
        String carBodyType,

        @Schema(description = "Estilo da moto caso o veículo seja uma moto", example = "Big Trail")
        String motorcycleStyleType,

        @Schema(description = "Cor do veículo.", example = "Vermelho")
        @NotBlank
        String color,

        @Schema(description = "Documento do dono do veículo (CPF/CNPJ).", example = "012.234.567-89")
        @NotBlank
        String document
) {
}
