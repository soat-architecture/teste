package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ServiceRequestDTO(

        @Schema(description = "Nome do serviço", example = "Troca de óleo")
        String name,

        @Schema(description = "Descrição do serviço", example = "Troca de óleo de um automóvel")
        String description,

        @Schema(description = "Valor base do serviço", example = "10.00")
        BigDecimal basePrice) { }
