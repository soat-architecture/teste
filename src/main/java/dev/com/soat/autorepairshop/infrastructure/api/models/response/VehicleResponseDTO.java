package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Schema(description = "Representa dados de um veículo.")
public record VehicleResponseDTO(

        @Schema(description = "Placa do veículo.", example = "ABC-1234 ou ABC1D34")
        String licensePlate,

        @Schema(description = "Marca do veículo.", example = "Ford")
        String brand,

        @Schema(description = "Modelo do veículo.", example = "Focus")
        String model,

        @Schema(description = "Ano de fabricação do veículo.", example = "2018")
        Integer manufactureYear,

        @Schema(description = "Tipo do veículo", example = "Carro ou Moto")
        String vehicleType,

        @Schema(description = "Tipo da carroceria caso o veículo seja um carro", example = "Hatch")
        String carBodyType,

        @Schema(description = "Estilo da moto caso o veículo seja uma moto", example = "Big Trail")
        String motorcycleStyleType,

        @Schema(description = "Cor do veículo.", example = "Vermelho")
        String color,

        @Schema(description = "Documento do dono do veículo (CPF/CNPJ).", example = "012.234.567-89")
        String document,

        @Schema(description = "Data de criação do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização do funcionário.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime updatedAt,

        @Schema(description = "Flag para identifica se um veículo está ativo no sistema.", example = "true ou false")
        boolean active
) {
}
