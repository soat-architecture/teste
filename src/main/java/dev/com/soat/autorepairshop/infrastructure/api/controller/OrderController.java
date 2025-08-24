package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.order.AssignmentOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.CalculateAverageServiceExecutionTimeUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.CreateOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.DeliveryOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.FindOrderDetailsUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.FindOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.FinishOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.GetOrderStatusUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.GetOrdersUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.ModifierEmployeeOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.StartOrderUseCase;
import dev.com.soat.autorepairshop.application.usecase.order.dto.AverageServiceExecutionTimeDTO;
import dev.com.soat.autorepairshop.application.usecase.order.dto.OrderDetailsOutputDTO;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.OrderMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.OrderRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.AverageServiceExecutionTimeResponse;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.OrderDetailsResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.OrderResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.OrderStatusResponse;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.SERVICE_ORDERS_TAG;

@Tag(name = SERVICE_ORDERS_TAG, description = "Operações relacionadas às ordens de serviço")
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final JwtTokenUtils jwtTokenUtils;
    private final CreateOrderUseCase createOrderUseCase;
    private final CalculateAverageServiceExecutionTimeUseCase calculateAverageServiceExecutionTimeUseCase;
    private final GetOrderStatusUseCase getOrderStatusUseCase;
    private final GetOrdersUseCase getOrdersUseCase;
    private final AssignmentOrderUseCase assignmentOrderUseCase;
    private final StartOrderUseCase startOrderUseCase;
    private final FinishOrderUseCase finishOrderUseCase;
    private final DeliveryOrderUseCase deliveryOrderUseCase;
    private final ModifierEmployeeOrderUseCase modifierEmployeeOrderUseCase;
    private final FindOrderUseCase findOrderUseCase;
    private final FindOrderDetailsUseCase findOrderDetailsUseCase;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Operation(
        summary = "Criação de uma nova ordem de serviço",
        description = """
            Este endpoint é responsável pela gravação de uma nova ordem de serviço na base de dados.
            Os dados fornecidos são validados e, em caso de sucesso, a ordem de serviço é persistida na base de dados.
        """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Corpo da requisição contendo os dados da ordem de serviço",
            required = true
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados fornecidos na requisição são inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
        }
    )
    public DefaultResponseDTO<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO dto, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        final var input = OrderMapper.map(dto, employeeId);
        final var output = createOrderUseCase.execute(input);
        final var response = OrderMapper.map(output);
        return DefaultResponseDTO.created(response);
    }

    @GetMapping("/average-service-execution-time")
    @Operation(
            summary = "Calcula o tempo médio de execução dos serviços",
            description = "Este endpoint retorna o tempo médio, em segundos, que leva para um serviço ser concluído.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tempo médio de execução retornado com sucesso"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json"))
            }
    )
    public DefaultResponseDTO<AverageServiceExecutionTimeResponse> getAverageServiceExecutionTime() {
        AverageServiceExecutionTimeDTO time = calculateAverageServiceExecutionTimeUseCase.execute();
        var response = new AverageServiceExecutionTimeResponse(time.seconds(), time.minutes(), time.hours(), time.formattedTime());
        return DefaultResponseDTO.success(response);
    }

    @GetMapping("/{id}/status")
    @Operation(
        summary = "Consulta o status de uma ordem de serviço",
        description = """
            Este endpoint é responsável por consultar o status de uma ordem de serviço na base de dados.
        """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Status da ordem de serviço consultado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada"),
            @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
        }
    )
    public DefaultResponseDTO<OrderStatusResponse> getStatus(@PathVariable Long id) {
        OrderStatusType status = getOrderStatusUseCase.execute(id);
        return DefaultResponseDTO.success(new OrderStatusResponse(status));
    }

    @PostMapping("/assignment/{serviceOrderId}")
    @Operation(
        summary = "Realizar a atribuição há ordem de serviço.",
        description = """
            Este endpoint é responsável por atribuir o mecânico a ordem de serviço.
        """,
        responses = {
                @ApiResponse(responseCode = "204", description = "Atribuição realizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço já atribuída a algum funcionário."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço com status diferente do esperado."),
                @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
                @ApiResponse(responseCode = "500", description = "Erro desconhecido.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MECHANICAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignment(@PathVariable final Long serviceOrderId, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        assignmentOrderUseCase.execute(serviceOrderId, employeeId);
    }

    @PostMapping("/start/{serviceOrderId}")
    @Operation(
        summary = "Realiza a iniciação da ordem de serviço.",
        description = """
            Este endpoint é responsável por realizar a iniciação de uma ordem de serviço.
        """,
        responses = {
                @ApiResponse(responseCode = "204", description = "Inicialização realizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço não atribuída ao funcionário."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço com status diferente do esperado."),
                @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                @ApiResponse(responseCode = "500", description = "Erro desconhecido.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MECHANICAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void start(@PathVariable final Long serviceOrderId, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        startOrderUseCase.execute(serviceOrderId, employeeId);
    }

    @PostMapping("/finish/{serviceOrderId}")
    @Operation(
        summary = "Realiza a finalização da ordem de serviço.",
        description = """
            Este endpoint é responsável por realizar a finalização de uma ordem de serviço.
        """,
        responses = {
                @ApiResponse(responseCode = "204", description = "Finalização realizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço não atribuída ao funcionário."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço com status diferente do esperado."),
                @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                @ApiResponse(responseCode = "500", description = "Erro desconhecido.", content = @Content(mediaType = "application/json"))
        }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MECHANICAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void finish(@PathVariable final Long serviceOrderId, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        finishOrderUseCase.execute(serviceOrderId, employeeId);
    }

    @PostMapping("/delivery/{serviceOrderId}")
    @Operation(
        summary = "Realiza a entrega de uma ordem de serviço.",
        description = """
            Este endpoint é responsável por realizar a entrega de uma ordem de serviço.
        """,
        responses = {
                @ApiResponse(responseCode = "204", description = "Entrega realizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço com status diferente do esperado."),
                @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                @ApiResponse(responseCode = "500", description = "Erro desconhecido.", content = @Content(mediaType = "application/json"))
        }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delivery(@PathVariable final Long serviceOrderId) {
        deliveryOrderUseCase.execute(serviceOrderId);
    }

    @PatchMapping("/update-employee")
    @Operation(
        summary = "Realiza a alteração de um funcionário a ordem de serviço.",
        description = """
            Este endpoint é responsável por realizar a alteração de um funcionário atrelado a uma ordem de serviço.
        """,
        responses = {
                @ApiResponse(responseCode = "204", description = "Atualização realizada com sucesso."),
                @ApiResponse(responseCode = "400", description = "Ordem de serviço com status diferente do esperado."),
                @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                @ApiResponse(responseCode = "500", description = "Erro desconhecido.", content = @Content(mediaType = "application/json"))
        }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void updateEmployee(@RequestHeader final Long orderId,
                               @RequestHeader final Long newEmployeeId) {
        modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId);
    }

    @GetMapping
    @Operation(
            summary = "Lista as ordens de serviço registradas",
            description = """
                    Endpoint responsável pela consulta de ordens de serviço registradas na base de dados.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista as ordens de serviço encontradas"),
                    @ApiResponse(responseCode = "404", description = "Ordens de serviço não encontradas"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    public DefaultResponseDTO<List<OrderResponseDTO>> listOrders(@RequestParam(required = false) String status, @RequestParam(required = false) Long employeeId) {
        List<OrderOutputDTO> result = getOrdersUseCase.execute(status, employeeId);
        List<OrderResponseDTO> response = result.stream().map(OrderMapper::map).toList();
        return DefaultResponseDTO.success(response);
    }

    @GetMapping("/await-executions")
    @Operation(
            summary = "Lista as ordens de serviço que estão pendentes de execução.",
            description = """
                    Endpoint responsável pela consulta de ordens de serviço pendentes de execução na base de dados.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ordens de serviço encontradas."),
                    @ApiResponse(responseCode = "404", description = "Ordens de serviço não encontradas."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor.", content = @Content(mediaType = "application/json"))
            }
    )
    public DefaultResponseDTO<List<OrderResponseDTO>> findAllAwaitExecution() {
        List<OrderOutputDTO> result = getOrdersUseCase.execute(OrderStatusType.RECEBIDA.getDescription(), null);
        List<OrderResponseDTO> response = result.stream().map(OrderMapper::map).toList();
        return DefaultResponseDTO.success(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Busca uma ordem de serviço pelo seu ID",
            description = """
            Este endpoint é responsável por consultar uma ordem de serviço com base em seu ID.
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ordem de serviço recuperada"),
                    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    public DefaultResponseDTO<OrderResponseDTO> findOrder(@PathVariable Long id) {
        OrderOutputDTO outputDTO = findOrderUseCase.execute(id);
        OrderResponseDTO responseDTO = OrderMapper.map(outputDTO);
        return DefaultResponseDTO.success(responseDTO);
    }

    @GetMapping("/{id}/details")
    @Operation(
            summary = "Busca uma visão detalhada da ordem de serviço conforme seu ID",
            description = """
            Este endpoint é responsável por consultar uma ordem de serviço com base em seu ID e apresentar dados relacionados a ela.
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ordem de serviço recuperada"),
                    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    public DefaultResponseDTO<OrderDetailsResponseDTO> findDetailedOrder (@PathVariable Long id){
        OrderDetailsOutputDTO result = findOrderDetailsUseCase.execute(id);
        OrderDetailsResponseDTO response = OrderMapper.map(result);
        return DefaultResponseDTO.success(response);
    }
}