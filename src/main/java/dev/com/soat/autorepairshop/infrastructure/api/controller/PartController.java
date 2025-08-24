package dev.com.soat.autorepairshop.infrastructure.api.controller;


import dev.com.soat.autorepairshop.application.usecase.part.CreatePartUseCase;
import dev.com.soat.autorepairshop.application.usecase.part.DeletePartUseCase;
import dev.com.soat.autorepairshop.application.usecase.part.FindAllPartPageableUseCase;
import dev.com.soat.autorepairshop.application.usecase.part.FindPartUseCase;
import dev.com.soat.autorepairshop.application.usecase.part.UpdatePartUseCase;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.PartMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.PartRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.PartResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.PARTS_TAG;

@Tag(name = PARTS_TAG, description = "Operações relacionadas às peças e insumos.")
@RestController
@RequestMapping("/v1/parts")
@RequiredArgsConstructor
public class PartController {

    private final CreatePartUseCase createPartUseCase;
    private final UpdatePartUseCase updatePartUseCase;
    private final DeletePartUseCase deletePartUseCase;
    private final FindPartUseCase findPartUseCase;
    private final FindAllPartPageableUseCase findAllPartsPageableUseCase;

    @PostMapping
    @Operation(
            summary = "Criação de uma nova peça ou insumo no banco de dados",
            description = """
                         Este endpoint é responsável pela gravação de uma nova peça ou insumo na base de dados.
                         Os campos obrigatórios são validados e o SKU precisa ser único.
                         """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados da peça ou insumo",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Peça criada com sucesso"),
                    @ApiResponse(responseCode = "409", description = "Conflito: já existe uma peça com o SKU informado"),
                    @ApiResponse(responseCode = "400", description = "Erro de domínio ou validação nos dados da peça"),
                    @ApiResponse(responseCode = "500", description = "Erro interno desconhecido")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponseDTO<DefaultDataDTO> create(@Valid @RequestBody PartRequestDTO body) {
        final var application = PartMapper.map(body);
        createPartUseCase.execute(application);
        return DefaultResponseDTO.created();
    }

    @GetMapping("/{identifier}")
    @Operation(
            summary = "Consulta de uma peça ou insumo por ID",
            description = """
                         Recupera os dados de uma peça ou insumo com base no ID informado.
                         Retorna 404 caso não exista uma peça com o ID fornecido.
                         """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Peça encontrada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Peça com o ID fornecido não encontrada"),
                    @ApiResponse(responseCode = "400", description = "Erro de domínio"),
                    @ApiResponse(responseCode = "500", description = "Erro interno desconhecido")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<PartResponseDTO> read(@PathVariable Long identifier) {
        var domain = findPartUseCase.execute(identifier);
        return DefaultResponseDTO.success(PartMapper.map(domain));
    }

    @GetMapping("/all")
    @Operation(
            summary = "Busca paginada de peças ou insumos",
            description = "Endpoint responsável por realizar a busca paginada das peças ou insumos cadastrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
                    @ApiResponse(responseCode = "500", description = "Erro interno desconhecido")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<PageInfoGenericUtils<PartResponseDTO>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        final var domains = findAllPartsPageableUseCase.execute(page, size, sortBy, direction);

        final var responses = domains.content()
                .stream()
                .map(PartMapper::map)
                .toList();

        final var pageResponse = new PageInfoGenericUtils<>(
                responses,
                domains.pageNumber(),
                domains.pageSize(),
                domains.totalElements(),
                domains.totalPages()
        );

        return DefaultResponseDTO.success(pageResponse);
    }

    @PutMapping("/{identifier}")
    @Operation(
            summary = "Atualização de uma peça ou insumo existente",
            description = """
                         Atualiza os dados de uma peça ou insumo com base no ID.
                         O registro precisa estar ativo e o SKU, se alterado, não pode se repetir.
                         """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados atualizados da peça ou insumo",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Peça atualizada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Peça com o ID fornecido não encontrada"),
                    @ApiResponse(responseCode = "409", description = "Conflito: SKU já utilizado por outra peça"),
                    @ApiResponse(responseCode = "400", description = "Peça inativa ou erro de validação"),
                    @ApiResponse(responseCode = "500", description = "Erro interno desconhecido")
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable Long identifier, @Valid @RequestBody PartRequestDTO body) {
        final var application = PartMapper.map(body);
        updatePartUseCase.execute(identifier, application);
    }

    @DeleteMapping("/{identifier}")
    @Operation(
            summary = "Exclusão lógica de uma peça ou insumo",
            description = """
                         Este endpoint realiza a exclusão lógica (inativação) de uma peça ou insumo com base no ID.
                         A exclusão só é possível se o registro existir e estiver ativo.
                         """,
            responses = {
                    @ApiResponse(responseCode = "204", description = "Peça inativada com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Peça com o ID fornecido não encontrada"),
                    @ApiResponse(responseCode = "400", description = "Erro de domínio"),
                    @ApiResponse(responseCode = "409", description = "Peça já está inativa"),
                    @ApiResponse(responseCode = "500", description = "Erro interno desconhecido")
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long identifier) {
        deletePartUseCase.execute(identifier);
    }
}
