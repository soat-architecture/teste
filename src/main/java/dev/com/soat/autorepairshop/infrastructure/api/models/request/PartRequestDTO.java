package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Representa os dados de uma peça ou insumo.")
public record PartRequestDTO(

        @Schema(description = "Nome da peça", example = "Pastilha de Freio")
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @Schema(description = "SKU da peça (identificador único)", example = "PAST-FREIO-123")
        @NotBlank(message = "O SKU é obrigatório.")
        String sku,

        @Schema(description = "Descrição detalhada da peça", example = "Pastilha de freio para veículos de passeio")
        String description,

        @Schema(description = "Marca do fabricante", example = "Bosch")
        String brand,

        @Schema(description = "Preço de venda da peça", example = "150.00")
        @NotNull(message = "O preço de venda é obrigatório.")
        @Positive(message = "O preço de venda deve ser um valor positivo.")
        BigDecimal sellingPrice,

        @Schema(description = "Preço de compra da peça", example = "100.00")
        @NotNull(message = "O preço de compra é obrigatório.")
        @Positive(message = "O preço de compra deve ser um valor positivo.")
        BigDecimal buyPrice

) {}
