package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representa os dados para criação ou atualização de um orçamento vinculado a uma ordem de serviço.")
public record BudgetRequestDTO(

        @Schema(description = "ID da ordem de serviço relacionada ao orçamento", example = "12345", required = true)
        Long serviceOrderId,

        @Schema(description = "Observações adicionais sobre o orçamento", example = "Cliente solicitou revisão completa.")
        String notes,

        @Schema(description = "Lista de itens que compõem o orçamento")
        List<BudgetItemRequestDTO> items

) {}
