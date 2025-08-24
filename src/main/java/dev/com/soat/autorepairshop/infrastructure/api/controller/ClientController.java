package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.mapper.ApplicationClientMapper;
import dev.com.soat.autorepairshop.application.usecase.client.CreateClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.DeleteClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.FindClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.UpdateClientUseCase;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ClientRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ClientResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.CLIENTS_TAG;

@Tag(name = CLIENTS_TAG, description = "Operações relacionadas aos clientes")
@RestController
@RequestMapping("/v1/client")
@RequiredArgsConstructor
public class ClientController {

    private final CreateClientUseCase createUseCase;
    private final FindClientUseCase findUseCase;
    private final UpdateClientUseCase updateUseCase;
    private final DeleteClientUseCase deleteUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Criação de um novo cliente no banco de dados",
        description = """
            Este endpoint é responsável pela gravação de um novo cliente na base de dados.
            Os dados fornecidos são validados e, em caso de sucesso, o cliente é persistido na base de dados.
        """,
        requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Corpo da requisição contendo os dados do cliente",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos na requisição são inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
        }
    )
    public DefaultResponseDTO<DefaultDataDTO> create(@Valid @RequestBody ClientRequestDTO body) {
        final var client = ApplicationClientMapper.map(body);
        createUseCase.execute(client);
        return DefaultResponseDTO.created();
    }

    @GetMapping("/{document}")
    @Operation(
        summary = "Busca de um cliente pelo documento",
        description = """
            Este endpoint é responsável por encontrar um cliente na base de dados através do seu documento (CPF/CNPJ).
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Registro do cliente com o documento informado encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Registro do cliente com o documento informado não encontrado", content = @Content)
        }
    )
    public DefaultResponseDTO<ClientResponseDTO> read(@PathVariable String document) {
        var client = this.findUseCase.execute(document);
        return DefaultResponseDTO.success(ApplicationClientMapper.map(client));
    }

    @PutMapping("/{document}")
    @Operation(
        summary = "Atualização de um cliente pelo documento",
        description = """
            Este endpoint é responsável pela atualização de um cliente na base de dados através do seu documento (CPF/CNPJ).
        """,
        requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Corpo da requisição contendo os dados do cliente a serem atualizados",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos na requisição são inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
        }
    )
    public DefaultResponseDTO<DefaultDataDTO> update(@PathVariable String document, @Valid @RequestBody ClientRequestDTO body) {
        final var client = ApplicationClientMapper.map(body);
        updateUseCase.execute(document, client);
        return DefaultResponseDTO.ok();
    }

    @DeleteMapping("/{document}")
    @Operation(
        summary = "Exclusão de um cliente pelo documento",
        description = """
            Este endpoint é responsável por realizar a exlcusão do cliente na base de dados através do seu documento (CPF/CNPJ).
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Cliente excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado")
        }
    )
    public DefaultResponseDTO<DefaultDataDTO> delete(@PathVariable String document) {
        deleteUseCase.execute(document);
        return DefaultResponseDTO.ok();
    }

}
