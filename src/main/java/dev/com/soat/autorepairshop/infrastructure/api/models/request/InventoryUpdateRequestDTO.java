package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import dev.com.soat.autorepairshop.domain.enums.MovementType;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Representa dados de estoque de peças/insumos")
public record InventoryUpdateRequestDTO(
    @Schema(description = "Quantidade da adição/remoção do estoque da peça/insumo.", example = "100")
    Integer quantityChanged,
    @Schema(description = "Tipo da modificação do estoque.", example = "INBOUND ou OUTBOUND_SALE")
    MovementType movementType
){ }