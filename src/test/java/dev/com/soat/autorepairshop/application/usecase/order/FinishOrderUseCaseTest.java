package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.utils.OrderValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinishOrderUseCaseTest {

    @Mock
    private OrderValidationUtils orderValidationUtils;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @InjectMocks
    private FinishOrderUseCase finishOrderUseCase;

    @Test
    @DisplayName("Deve finalizar ordem de serviço com sucesso quando ordem de serviço estiver em execução")
    void shouldFinishOrderSuccessfullyWhenOrderIsInExecution() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_EXECUCAO,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);
        doNothing().when(orderHistoryGateway).save(any(OrderHistoryDomain.class));

        // When/Then
        assertDoesNotThrow(() -> finishOrderUseCase.execute(orderId, employeeId));

        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway).save(any(OrderDomain.class));
        verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));

        assertEquals(OrderStatusType.FINALIZADA, order.getStatus());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço não estiver em execução - status RECEBIDA")
    void shouldThrowDomainExceptionWhenOrderIsNotInExecution_StatusReceived() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.is.not.execution", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço não estiver em execução - status APROVADA")
    void shouldThrowDomainExceptionWhenOrderIsNotInExecution_StatusApproved() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.APROVADA,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.is.not.execution", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço não estiver em execução - status EM_DIAGNOSTICO")
    void shouldThrowDomainExceptionWhenOrderIsNotInExecution_StatusInDiagnosis() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_DIAGNOSTICO,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.is.not.execution", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço não estiver em execução - status FINALIZADA")
    void shouldThrowDomainExceptionWhenOrderIsNotInExecution_StatusFinished() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.FINALIZADA,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.is.not.execution", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço não estiver em execução - status ENTREGUE")
    void shouldThrowDomainExceptionWhenOrderIsNotInExecution_StatusDelivered() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.ENTREGUE,
                "Test notes",
                employeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId)).thenReturn(order);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.is.not.execution", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando validateOrderIsAssignedToEmployee lançar NotFoundException")
    void shouldPropagateExceptionWhenValidationThrowsNotFoundException() {
        // Given
        final Long orderId = 999L;
        final Long employeeId = 10L;

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId))
                .thenThrow(new NotFoundException("order.not.found"));

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.not.found", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando validateOrderIsAssignedToEmployee lançar DomainException")
    void shouldPropagateExceptionWhenValidationThrowsDomainException() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 999L;

        when(orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId))
                .thenThrow(new DomainException("order.not.assigned.to.employee"));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> finishOrderUseCase.execute(orderId, employeeId)
        );

        assertEquals("order.not.assigned.to.employee", exception.getMessage());
        verify(orderValidationUtils).validateOrderIsAssignedToEmployee(orderId, employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }
}