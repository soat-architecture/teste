
package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.usecase.budget.PrepareBudgetEmailUseCase;
import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.service.NotificationService;
import dev.com.soat.autorepairshop.mock.BudgetItemMock;
import dev.com.soat.autorepairshop.mock.BudgetMock;
import dev.com.soat.autorepairshop.mock.PartMock;
import dev.com.soat.autorepairshop.mock.ServiceMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para NotificationController")
class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PrepareBudgetEmailUseCase prepareBudgetEmailUseCase;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
    }

    @Test
    @DisplayName("Deve enviar email do orçamento com sucesso e retornar status 200")
    void sendBudgetEmail_shouldReturnOk_whenSuccessful() throws Exception {
        // given
        Long serviceOrderId = 1L;
        final var aggregateRoot = createMockBudgetAggregateRoot();

        when(prepareBudgetEmailUseCase.execute(serviceOrderId)).thenReturn(aggregateRoot);
        doNothing().when(notificationService).sendBudgetNotification(aggregateRoot);

        // when/then
        mockMvc.perform(post("/v1/notifications/budget/send/{serviceOrderId}", serviceOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.message").value("Successfully in the request. No given response."));

        verify(prepareBudgetEmailUseCase).execute(serviceOrderId);
        verify(notificationService).sendBudgetNotification(any(BudgetAggregateRoot.class));
    }

    @Test
    @DisplayName("Deve retornar status 404 quando o orçamento não for encontrado")
    void sendBudgetEmail_shouldReturnNotFound_whenBudgetDoesNotExist() throws Exception {
        // given
        Long serviceOrderId = 1L;
        when(prepareBudgetEmailUseCase.execute(serviceOrderId))
                .thenThrow(new NotFoundException("budget.not.found"));

        // when/then
        mockMvc.perform(post("/v1/notifications/budget/send/{serviceOrderId}", serviceOrderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data.error").value("Not Found Error"));

        verify(prepareBudgetEmailUseCase).execute(serviceOrderId);
    }

    @Test
    @DisplayName("Deve retornar status 500 quando ocorrer erro interno")
    void sendBudgetEmail_shouldReturnInternalServerError_whenUnexpectedErrorOccurs() throws Exception {
        // given
        Long serviceOrderId = 1L;
        when(prepareBudgetEmailUseCase.execute(serviceOrderId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // when/then
        mockMvc.perform(post("/v1/notifications/budget/send/{serviceOrderId}", serviceOrderId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data.error").value("Generic Error"));

        verify(prepareBudgetEmailUseCase).execute(serviceOrderId);
    }

    private BudgetAggregateRoot createMockBudgetAggregateRoot() {
        final var client = new ClientDomain(1L, "Teste", "71019345012", "(11) 97653-2346",
                "teste@teste.com", ClientStatus.ACTIVE, null, null);

        final var budget = BudgetMock.buildDomain();
        final var service = ServiceMock.buildDomain();
        final var part = PartMock.buildDomain();
        final var item = BudgetItemMock.buildDomain();
        final var itemRoot = BudgetItemAggregateRoot.create(
                service,
                part,
                item
        );



        return BudgetAggregateRoot.create(
                client,
                budget,
                List.of(itemRoot)
        );
    }
}