package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.usecase.service.CreateServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.DeleteServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.FindAllServicesPageableUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.FindServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.UpdateServiceUseCase;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.ServiceMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ServiceRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ServiceResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.SERVICES_TAG;

@Tag(name = SERVICES_TAG, description = "Operações relacionadas aos serviços prestados pela oficina.")
@RestController
@RequestMapping("/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final FindServiceUseCase findServiceUseCase;
    private final CreateServiceUseCase createServiceUseCase;
    private final DeleteServiceUseCase deleteServiceUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final FindAllServicesPageableUseCase findAllServicesPageableUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Criação de um novo serviço",
            description = """
                Endpoint responsável pela criação de um novo serviço.
                Caso os dados informados sejam válidos, é registrado um novo serviço.
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do novo serviço.",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Novo serviço criado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos.")
            })
    public void create(@RequestBody ServiceRequestDTO body){
        final ServiceInputDTO input = ServiceMapper.map(body);
        createServiceUseCase.execute(input);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Busca de serviço por ID do serviço",
            description = """
                Endpoint responsável por buscar um serviço no banco de dados pelo seu ID.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Serviço encontrado."),
                    @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
            })
    public DefaultResponseDTO<ServiceResponseDTO> find(@PathVariable Long id){
        final ServiceResponseDTO response = ServiceMapper.map(findServiceUseCase.execute(id));
        return DefaultResponseDTO.success(response);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Busca paginada de todos os serviços",
            description = "Endpoint responsável por realizar a busca paginada de todos os serviços cadastrados.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor.")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<PageInfoGenericUtils<ServiceResponseDTO>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        final var domains = findAllServicesPageableUseCase.execute(page, size, sortBy, direction);

        final var userResponses = domains.content()
                .stream()
                .map(ServiceMapper::map)
                .toList();

        final var pageResponse = new PageInfoGenericUtils<>(
                userResponses,
                domains.pageNumber(),
                domains.pageSize(),
                domains.totalElements(),
                domains.totalPages()
        );
        return DefaultResponseDTO.success(pageResponse);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Atualização de um serviço",
            description = """
                Endpoint responsável pela atualização de um serviço.
            """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da requisição de atualização do serviço.",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos"),
                    @ApiResponse(responseCode = "404", description = "Serviço não encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido")
            }
        )
    public void update(@PathVariable Long id, @RequestBody ServiceRequestDTO body){
        final ServiceInputDTO input = ServiceMapper.map(body);
        updateServiceUseCase.execute(id, input);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Exclusão de serviço por ID",
            description = """
                Endpoint responsável pela exclusão de um serviço com base no ID informado.
            """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Serviço removido com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Serviço não encontrado.")
            })
    public void delete(@PathVariable Long id){
        deleteServiceUseCase.execute(id);
    }
}
