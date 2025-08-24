package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignmentOrderUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @InjectMocks
    private AssignmentOrderUseCase assignmentOrderUseCase;

    @Test
    @DisplayName("Deve atribuir ordem de serviço com sucesso quando todos os dados estão válidos")
    void shouldAssignOrderSuccessfully() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 2L;

        final var order = mock(OrderDomain.class);
        final var employee = mock(UserDomain.class);
        final var history = mock(OrderHistoryDomain.class);

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(employeeId)).thenReturn(employee);
        when(order.isOrderInInitialStatus()).thenReturn(false);
        when(employee.getIdentifier()).thenReturn(employeeId);

        try (MockedStatic<ApplicationOrderHistoryMapper> mapperMock = mockStatic(ApplicationOrderHistoryMapper.class)) {
            mapperMock.when(() -> ApplicationOrderHistoryMapper.map(order)).thenReturn(history);

            // When/Then
            assertDoesNotThrow(() -> assignmentOrderUseCase.execute(orderId, employeeId));

            verify(orderGateway).findById(orderId);
            verify(userGateway).findByUserId(employeeId);
            verify(order).assignEmployee(employeeId);
            verify(orderGateway).save(order);
            verify(orderHistoryGateway).save(history);
        }
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem de serviço não for encontrado")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 2L;

        when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(
                NotFoundException.class,
                () -> assignmentOrderUseCase.execute(orderId, employeeId)
        );

        verify(orderGateway).findById(orderId);
        verify(userGateway, never()).findByUserId(anyLong());
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando funcionário não for encontrado")
    void shouldThrowNotFoundExceptionWhenEmployeeNotFound() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 2L;

        final var order = mock(OrderDomain.class);

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(employeeId)).thenReturn(null);

        // When/Then
        assertThrows(
                NotFoundException.class,
                () -> assignmentOrderUseCase.execute(orderId, employeeId)
        );

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(employeeId);
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ordem de serviço está em status inicial")
    void shouldThrowDomainExceptionWhenOrderInInitialStatus() {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 2L;

        final var order = mock(OrderDomain.class);
        final var employee = mock(UserDomain.class);

        when(orderGateway.findById(orderId)).thenReturn(Optional.of(order));
        when(userGateway.findByUserId(employeeId)).thenReturn(employee);
        when(order.isOrderInInitialStatus()).thenReturn(true);

        // When/Then
        assertThrows(
                DomainException.class,
                () -> assignmentOrderUseCase.execute(orderId, employeeId)
        );

        verify(orderGateway).findById(orderId);
        verify(userGateway).findByUserId(employeeId);
        verify(order).isOrderInInitialStatus();
        verify(orderGateway, never()).save(any(OrderDomain.class));
        verify(orderHistoryGateway, never()).save(any(OrderHistoryDomain.class));
    }
}
