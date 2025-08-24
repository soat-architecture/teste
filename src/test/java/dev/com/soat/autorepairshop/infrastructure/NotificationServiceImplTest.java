package dev.com.soat.autorepairshop.infrastructure;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.enums.EmailMessageType;
import dev.com.soat.autorepairshop.domain.enums.NotificationStatusType;
import dev.com.soat.autorepairshop.infrastructure.adapter.EmailAdapter;
import dev.com.soat.autorepairshop.infrastructure.adapter.TemplateEmailAdapter;
import dev.com.soat.autorepairshop.infrastructure.repository.NotificationOrderRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.NotificationOrderEntity;
import dev.com.soat.autorepairshop.infrastructure.service.NotificationServiceImpl;
import dev.com.soat.autorepairshop.mock.BudgetAggregateRootMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private EmailAdapter emailAdapter;

    @Mock
    private NotificationOrderRepository notificationOrderRepository;

    @Mock
    private TemplateEmailAdapter templateEmailAdapter;

    @Captor
    private ArgumentCaptor<NotificationOrderEntity> notificationOrderCaptor;

    private BudgetAggregateRoot budgetAggregateRoot;
    private static final String COMPANY_EMAIL = "company@test.com";
    private static final String CLIENT_EMAIL = "teste@teste.com";
    private static final Long CLIENT_ID = 1L;
    private static final Long SERVICE_ORDER_ID = 1L;
    private static final String TEMPLATE_MESSAGE = "Budget notification message";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "companyEmail", COMPANY_EMAIL);
        budgetAggregateRoot = BudgetAggregateRootMock.buildDomain();

        when(templateEmailAdapter.buildBudgetMessage(anyString(), eq(budgetAggregateRoot)))
                .thenReturn(TEMPLATE_MESSAGE);
    }

    @Test
    @DisplayName("Deve enviar notificação de orçamento com sucesso")
    @SuppressWarnings("squid:S6068")
    void shouldSendBudgetNotificationSuccessfully() {
        // Act
        notificationService.sendBudgetNotification(budgetAggregateRoot);

        // Assert
        verify(emailAdapter).send(
                eq(COMPANY_EMAIL),
                eq(CLIENT_EMAIL),
                eq(EmailMessageType.BUDGET_EMAIL_TITLE.getSubject()),
                eq(TEMPLATE_MESSAGE),
                eq(EmailMessageType.BUDGET_EMAIL_TITLE.getIsHtml())
        );

        verify(notificationOrderRepository).save(notificationOrderCaptor.capture());
        NotificationOrderEntity savedNotification = notificationOrderCaptor.getValue();

        assertEquals(SERVICE_ORDER_ID, savedNotification.getServiceOrderId());
        assertEquals(CLIENT_ID, savedNotification.getClientId());
        assertEquals(EmailMessageType.BUDGET_EMAIL_TITLE.getChannel(), savedNotification.getChannel());
        assertEquals(TEMPLATE_MESSAGE, savedNotification.getMessage());
        assertEquals(NotificationStatusType.DELIVERED, savedNotification.getStatus());
    }

    @Test
    @DisplayName("Deve salvar notificação com status FAILED quando ocorrer erro no envio")
    void shouldSaveFailedNotificationWhenErrorOccurs() {
        // Arrange
        doThrow(new RuntimeException("Email sending failed"))
                .when(emailAdapter)
                .send(anyString(), anyString(), anyString(), anyString(), anyBoolean());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                notificationService.sendBudgetNotification(budgetAggregateRoot)
        );

        verify(notificationOrderRepository).save(notificationOrderCaptor.capture());
        NotificationOrderEntity savedNotification = notificationOrderCaptor.getValue();

        assertEquals(SERVICE_ORDER_ID, savedNotification.getServiceOrderId());
        assertEquals(CLIENT_ID, savedNotification.getClientId());
        assertEquals(EmailMessageType.BUDGET_EMAIL_TITLE.getChannel(), savedNotification.getChannel());
        assertEquals(TEMPLATE_MESSAGE, savedNotification.getMessage());
        assertEquals(NotificationStatusType.FAILED, savedNotification.getStatus());
    }

    @Test
    @DisplayName("Deve usar template correto para mensagem de orçamento")
    @SuppressWarnings("squid:S6068")
    void shouldUseCorrectTemplateForBudgetMessage() {
        // Act
        notificationService.sendBudgetNotification(budgetAggregateRoot);

        // Assert
        verify(templateEmailAdapter).buildBudgetMessage(
                eq(EmailMessageType.BUDGET_EMAIL_TITLE.getTitle()),
                eq(budgetAggregateRoot)
        );
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer erro no envio")
    void shouldPropagateExceptionWhenSendingFails() {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Email sending failed");
        doThrow(expectedException)
                .when(emailAdapter)
                .send(anyString(), anyString(), anyString(), anyString(), anyBoolean());

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () ->
                notificationService.sendBudgetNotification(budgetAggregateRoot)
        );

        assertEquals(expectedException, thrownException);
    }
}