package dev.com.soat.autorepairshop.application.usecase.order;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @InjectMocks
    private DeliveryOrderUseCase deliveryOrderUseCase;

    @Test
    @DisplayName("Deve entregar pedido com sucesso quando pedido estiver finalizado")
    void shouldDeliverOrderSuccessfullyWhenOrderIsFinished() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.FINALIZADA,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);
        doNothing().when(orderHistoryGateway).save(any(OrderHistoryDomain.class));

        // When/Then
        assertDoesNotThrow(() -> deliveryOrderUseCase.execute(orderId));

        verify(orderGateway).findById(orderId);
        verify(orderGateway).save(any(OrderDomain.class));
        verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));

        assertEquals(OrderStatusType.ENTREGUE, order.getStatus());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando pedido não for encontrado")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // Given
        final Long orderId = 999L;

        when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("order.not.found", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando pedido não estiver finalizado")
    void shouldThrowDomainExceptionWhenOrderIsNotFinished() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_EXECUCAO,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("order.is.not.finish", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando pedido estiver com status RECEBIDA")
    void shouldThrowDomainExceptionWhenOrderStatusIsReceived() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.RECEBIDA,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("order.is.not.finish", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando pedido estiver com status APROVADA")
    void shouldThrowDomainExceptionWhenOrderStatusIsApproved() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.APROVADA,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("order.is.not.finish", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando pedido estiver com status EM_DIAGNOSTICO")
    void shouldThrowDomainExceptionWhenOrderStatusIsInDiagnosis() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.EM_DIAGNOSTICO,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                null
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("order.is.not.finish", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve propagar exceção quando ocorrer erro no gateway de pedidos")
    void shouldPropagateExceptionWhenOrderGatewayThrowsException() {
        // Given
        final Long orderId = 1L;
        final var order = OrderDomain.restore(
                orderId,
                "35962726880",
                "ABC1234",
                OrderStatusType.FINALIZADA,
                "Test notes",
                1L,
                1L,
                1L,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(orderGateway.save(any(OrderDomain.class))).thenThrow(new RuntimeException("Database error"));

        // When/Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> deliveryOrderUseCase.execute(orderId)
        );

        assertEquals("Database error", exception.getMessage());
        verify(orderGateway).findById(orderId);
        verify(orderGateway).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }
}