package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetOrdersUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private GetOrdersUseCase getOrdersUseCase;

    @Test
    void execute_shouldReturnOrders_whenOrdersExist() {

        OrderDomain orderDomain = OrderDomain.restore(
                1L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderGateway.findAllOrders()).thenReturn(List.of(orderDomain));
        List<OrderOutputDTO> result = getOrdersUseCase.execute(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void execute_shouldThrowException_whenOrdersDoNotExist() {
        when(orderGateway.findAllOrders()).thenReturn(Collections.emptyList());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> getOrdersUseCase.execute(null, null)
        );

        assertEquals("No orders found", thrown.getMessage());
    }

    @Test
    void execute_shouldReturnOrders_whenOrdersExist_searchingForStatus() {

        OrderDomain orderDomain1 = OrderDomain.restore(
                1L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain2 = OrderDomain.restore(
                2L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.FINALIZADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain3 = OrderDomain.restore(
                3L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.EM_EXECUCAO,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain4 = OrderDomain.restore(
                4L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderGateway.findAllOrders()).thenReturn(List.of(orderDomain1, orderDomain2, orderDomain3, orderDomain4));
        List<OrderOutputDTO> result = getOrdersUseCase.execute("Aprovada", null);

        result.forEach(r -> {
            Assertions.assertEquals(r.status(), OrderStatusType.APROVADA.getDescription());
        });

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void execute_shouldReturnOrders_whenOrdersExist_searchingForEmployeeId() {

        OrderDomain orderDomain1 = OrderDomain.restore(
                1L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain2 = OrderDomain.restore(
                2L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.FINALIZADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain3 = OrderDomain.restore(
                3L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.EM_EXECUCAO,
                null,
                3L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain4 = OrderDomain.restore(
                4L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                2L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderGateway.findAllOrders()).thenReturn(List.of(orderDomain1, orderDomain2, orderDomain3, orderDomain4));
        List<OrderOutputDTO> result = getOrdersUseCase.execute(null, 1L);

        result.forEach(r -> {
            Assertions.assertEquals(1L, r.employeeIdentifier());
        });

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void execute_shouldReturnOrders_whenOrdersExist_searchingForStatusAndEmployeeId() {

        OrderDomain orderDomain1 = OrderDomain.restore(
                1L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain2 = OrderDomain.restore(
                2L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.FINALIZADA,
                null,
                1L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain3 = OrderDomain.restore(
                3L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.EM_EXECUCAO,
                null,
                3L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        OrderDomain orderDomain4 = OrderDomain.restore(
                4L,
                "09242194913",
                "ABC1D34",
                OrderStatusType.APROVADA,
                null,
                2L,
                1L,
                1L,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now());

        when(orderGateway.findAllOrders()).thenReturn(List.of(orderDomain1, orderDomain2, orderDomain3, orderDomain4));
        List<OrderOutputDTO> result = getOrdersUseCase.execute("Aprovada", 2L);

        result.forEach(r -> {
            Assertions.assertEquals(r.status(), OrderStatusType.APROVADA.getDescription());
            Assertions.assertEquals(2L, r.employeeIdentifier());
        });

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
