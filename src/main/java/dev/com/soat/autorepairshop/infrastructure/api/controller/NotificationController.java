package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.usecase.budget.PrepareBudgetEmailUseCase;
import dev.com.soat.autorepairshop.domain.service.NotificationService;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.NOTIFICATION_TAG;


@Tag(name = NOTIFICATION_TAG, description = "Operações relacionadas ao envio de email")
@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final PrepareBudgetEmailUseCase prepareBudgetEmailUseCase;
    private final NotificationService notificationService;

    @Operation(
            summary = "Envio do email para o cliente.",
            description = "Endpoint responsável por enviar um email contendo os dados do orçamento para o cliente aprovar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email enviado com sucesso."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    @PostMapping("/budget/send/{serviceOrderId}")
    public DefaultResponseDTO<DefaultDataDTO> sendBudgetEmail(@PathVariable Long serviceOrderId) {
        final var budgetAggregateRoot = prepareBudgetEmailUseCase.execute(serviceOrderId);
        notificationService.sendBudgetNotification(budgetAggregateRoot);
        return DefaultResponseDTO.ok();
    }
}
