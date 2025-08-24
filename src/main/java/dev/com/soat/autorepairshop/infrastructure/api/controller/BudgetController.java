package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.usecase.budget.ApproveBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.CreateBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.FindBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.NotApproveBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.UpdateBudgetUseCase;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.BudgetMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetDetailResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.BUDGETS_TAG;

@Tag(name = BUDGETS_TAG, description = "Operações relacionadas aos orçamentos")
@RestController
@RequestMapping("/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final JwtTokenUtils jwtTokenUtils;
    private final CreateBudgetUseCase createUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final FindBudgetUseCase findBudgetUseCase;
    private final ApproveBudgetUseCase approveBudgetUseCase;
    private final NotApproveBudgetUseCase notApproveBudgetUseCase;

    @PostMapping
    @Operation(
            summary = "Criação de um novo orçamento",
            description = """
                    Este endpoint é responsável pela gravação de um novo orçamento na base de dados.
                    Os dados fornecidos são validados e, em caso de sucesso, o orçamento é persistido na base de dados.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados do orçamento",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados fornecidos na requisição são inválidos"),
                    @ApiResponse(responseCode = "409", description = "Conflito: já existe um orçamento para a mesma OS"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody BudgetRequestDTO body, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        final var application = BudgetMapper.mapToNewBudget(body, employeeId);
        createUseCase.execute(application);
    }

    @PutMapping("/{budgetId}")
    @Operation(
            summary = "Criação de uma nova versão de um orçamento existente",
            description = """
                    Atualiza um orçamento criando **uma nova versão** baseada no orçamento existente.
                    O 'budgetId' deve ser informado na URL. O 'budgetId' no corpo é ignorado se tentar mudar.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados do orçamento para atualização (nova versão)",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nova versão criada com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Requisição inválida"),
                    @ApiResponse(responseCode = "404", description = "Orçamento ou funcionário não encontrados"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable Long budgetId, @Valid @RequestBody BudgetRequestDTO body, HttpServletRequest request) {
        final var cookie = jwtTokenUtils.getTokenFromCookie(request);
        final var employeeId = jwtTokenUtils.getIdentifierFromToken(cookie);
        final var application = BudgetMapper.map(body, employeeId, budgetId);
        updateBudgetUseCase.execute(application);
    }


    @Operation(
            summary = "Recebe email aprovado pelo cliente",
            description = "Endpoint responsável por receber a aprovação do orçamento pelo cliente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resposta recebida"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    @GetMapping("/approve/{serviceOrderId}")
    public DefaultResponseDTO<DefaultDataDTO> receiveApprovedBudget(@PathVariable Long serviceOrderId) {
        approveBudgetUseCase.execute(serviceOrderId);
        return DefaultResponseDTO.ok();
    }

    @Operation(
            summary = "Recebe email aprovado pelo cliente",
            description = "Endpoint responsável por receber a não aprovação do orçamento pelo cliente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resposta recebida"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    @GetMapping("/not-approve/{serviceOrderId}")
    public DefaultResponseDTO<DefaultDataDTO> receiveNotApprovedBudget(@PathVariable Long serviceOrderId) {
        notApproveBudgetUseCase.execute(serviceOrderId);
        return DefaultResponseDTO.ok();
    }

    @GetMapping("/{serviceOrderId}")
    @Operation(
            summary = "Recupera o orçamento atrelado a uma ordem de serviço",
            description = """
                    Endpoint responsável por recuperar o orçamento de uma ordem de serviço.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Orçamento recuperado com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
                    @ApiResponse(responseCode = "404", description = "Orçamento não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<BudgetDetailResponseDTO> findByServiceOrderId(@PathVariable Long serviceOrderId) {
        final var budget = findBudgetUseCase.execute(serviceOrderId);
        return DefaultResponseDTO.success(BudgetMapper.mapToResponseDTO(budget));
    }
}
