package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.CreateInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.FindInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.UpdateInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.FindInventoryMovementsUseCase;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryUpdateRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.INVENTORY_TAG;

@Tag(name= INVENTORY_TAG, description = "Operações relacionadas a movimentações de estoque de peças e insumos.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/inventory")
public class InventoryController {

    private final FindInventoryUseCase findUseCase;
    private final CreateInventoryUseCase createInventoryUseCase;
    private final FindInventoryMovementsUseCase findInventoryMovementsUseCase;
    private final UpdateInventoryUseCase updateInventoryUseCase;

    @GetMapping("/{partId}")
    @Operation(
            summary = "Busca o estoque de uma peça ou insumo pelo seu ID.",
            description = """
                Endpoint responsável pela recuperação dos dados de estoque de uma peça ou insumo com base em seu ID.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estoque da peça/insumo encontrado."),
                    @ApiResponse(responseCode = "404", description = "Estoque não encontrado.")
            })
    public DefaultResponseDTO<InventoryRequestDTO> read(@PathVariable Long partId){
        var inventory = this.findUseCase.execute(partId);
        InventoryRequestDTO response = InventoryApplicationMapper.map(inventory);
        return DefaultResponseDTO.success(response);
    }

    @GetMapping("/{partSku}/movements")
    @Operation(
            summary = "Busca movimentação de estoque de determinada peça/insumo",
            description = """
                Endpoint responsável por recuperar movimentações de estoque de uma peça ou insumo.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Movimentações de estoque encontradas."),
                    @ApiResponse(responseCode = "404", description = "Não foram encontradas movimentações de estoque para a peça/insumo.")
            })
    public DefaultResponseDTO<List<InventoryMovementOutputDTO>> readMovementHistory(@PathVariable String partSku){
        List<InventoryMovementOutputDTO> movements = findInventoryMovementsUseCase.execute(partSku);
        return DefaultResponseDTO.success(movements);
    }

    @PostMapping("/movements")
    @Operation(
            summary = "Registrar alteração de estoque",
            description = """
                Endpoint responsável por registrar uma alteração de estoque para determinada peça/insumo.
                Caso os dados da requisição sejam válidos, é criado um novo registro de estoque para a peça/insumo e registrada a movimentação.
                Na ocasião de já existir um estoque cadastrado para essa peça/insumo, o mesmo é atualizado conforme o tipo de movimentação informada na requisição.
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da requisição contendo os dados da peça/insumo.",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Estoque criado/atualizado."),
                    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            })
    public DefaultResponseDTO<InventoryRequestDTO> create(@RequestBody InventoryRequestDTO body) {
        final var inventory = InventoryApplicationMapper.map(body);
        createInventoryUseCase.execute(inventory);
        return DefaultResponseDTO.created(body);
    }

    @PatchMapping("/movements/{partId}")
    @Operation(
            summary = "Registrar alteração de estoque",
            description = """
                Endpoint responsável por registrar uma alteração de estoque para determinada peça/insumo.
                Caso os dados da requisição sejam válidos, é criado um novo registro de estoque para a peça/insumo e registrada a movimentação.
                Na ocasião de já existir um estoque cadastrado para essa peça/insumo, o mesmo é atualizado conforme o tipo de movimentação informada na requisição.
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da requisição contendo os dados da peça/insumo.",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Estoque criado/atualizado."),
                    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            })
    public DefaultResponseDTO<InventoryInputDTO> update(final @PathVariable Long partId, final @RequestBody InventoryUpdateRequestDTO body) {
        final var inventory = InventoryMovementMapper.map(partId, body);
        updateInventoryUseCase.execute(partId, inventory);
        return DefaultResponseDTO.success(inventory);
    }
}