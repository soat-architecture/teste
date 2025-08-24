package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModifierEmployeeOrderUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private ModifierEmployeeOrderUseCase modifierEmployeeOrderUseCase;

    @Test
    @DisplayName("Deve modificar funcionário do ordem de serviço com sucesso quando ordem de serviço e funcionário existirem")
    void shouldModifyEmployeeOrderSuccessfullyWhenOrderAndEmployeeExist() {
        // Given
        final Long orderId = 1L;
        final Long oldEmployeeId = 10L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                oldEmployeeId,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        // When/Then
        assertDoesNotThrow(() -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId));

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));

        assertEquals(newEmployeeId, order.getEmployeeId());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem de serviço não for encontrado")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // Given
        final Long orderId = 999L;
        final Long newEmployeeId = 20L;

        when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId)
        );

        assertEquals("order.not.found", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(userGateway, never()).findByUserId(anyLong());
        verify(orderGateway, never()).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando funcionário não for encontrado")
    void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 999L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(null);

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId)
        );

        assertEquals("user.not.found", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Deve modificar funcionário com sucesso para diferentes status de ordem de serviço - APROVADA")
    void shouldModifyEmployeeSuccessfullyForDifferentOrderStatus_Approved() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.APROVADA,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        // When/Then
        assertDoesNotThrow(() -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId));

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));

        assertEquals(newEmployeeId, order.getEmployeeId());
    }

    @Test
    @DisplayName("Deve modificar funcionário com sucesso para diferentes status de ordem de serviço - EM_DIAGNOSTICO")
    void shouldModifyEmployeeSuccessfullyForDifferentOrderStatus_InDiagnosis() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_DIAGNOSTICO,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        // When/Then
        assertDoesNotThrow(() -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId));

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));

        assertEquals(newEmployeeId, order.getEmployeeId());
    }

    @Test
    @DisplayName("Deve modificar funcionário com sucesso para diferentes status de ordem de serviço - EM_EXECUCAO")
    void shouldModifyEmployeeSuccessfullyForDifferentOrderStatus_InExecution() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_EXECUCAO,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        // When/Then
        assertDoesNotThrow(() -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId));

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));

        assertEquals(newEmployeeId, order.getEmployeeId());
    }

    @Test
    @DisplayName("Deve modificar funcionário com sucesso mesmo quando ordem de serviço já estiver finalizado")
    void shouldModifyEmployeeSuccessfullyEvenWhenOrderIsFinished() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.FINALIZADA,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        // When/Then
        assertDoesNotThrow(() -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId));

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));

        assertEquals(newEmployeeId, order.getEmployeeId());
    }

    @Test
    @DisplayName("Deve propagar exceção quando orderGateway.save lançar RuntimeException")
    void shouldPropagateExceptionWhenOrderGatewaySaveThrowsException() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        final var employee = UserDomain.restore(
                newEmployeeId,
                "Employee Name",
                "98765432100",
                "employee@test.com",
                "password123",
                LocalDateTime.now().minusDays(10),
                "MECHANICAL",
                "ACTIVE",
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(10)
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenReturn(employee);
        when(orderGateway.save(any(OrderDomain.class))).thenThrow(new RuntimeException("Database error"));

        // When/Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId)
        );

        assertEquals("Database error", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway).save(any(OrderDomain.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando userGateway.findByUserId lançar RuntimeException")
    void shouldPropagateExceptionWhenUserGatewayFindByUserIdThrowsException() {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;

        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                10L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(newEmployeeId)).thenThrow(new RuntimeException("User gateway error"));

        // When/Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> modifierEmployeeOrderUseCase.execute(orderId, newEmployeeId)
        );

        assertEquals("User gateway error", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(newEmployeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
    }
}