package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Representa dados de estoque de peças/insumos")
public record InventoryRequestDTO(
    @Schema(description = "Identificador da peça/insumo", example = "1")
    Long partId,

    @Schema(description = "Quantidade em estoque da peça/insumo", example = "100")
    Integer quantityOnHand

){ }