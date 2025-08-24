package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.usecase.order.dto.AverageServiceExecutionTimeDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CalculateAverageServiceExecutionTimeUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private CalculateAverageServiceExecutionTimeUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnZeroWhenNoCompletedOrders() {
        when(orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(Collections.emptyList());

        AverageServiceExecutionTimeDTO time = useCase.execute();

        assertEquals(0.0, time.seconds());
        assertEquals(0.0, time.minutes());
        assertEquals(0.0, time.hours());
        assertEquals("00:00:00", time.formattedTime());

    }

    @Test
    void shouldCalculateAverageTimeForOneCompletedOrder() {
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime completedAt = LocalDateTime.of(2023, 1, 1, 11, 0, 0);
        OrderDomain order = OrderDomain.restore(1L, "12345678909", "ABC1E67", OrderStatusType.FINALIZADA, "notes", 1L, 1L, 1L, createdAt, null, completedAt);

        when(orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(Collections.singletonList(order));

        AverageServiceExecutionTimeDTO time = useCase.execute();

        assertEquals(3600.0, time.seconds());
        assertEquals(60.0, time.minutes());
        assertEquals(1.0, time.hours());
        assertEquals("1:00:00", time.formattedTime());
    }

    @Test
    void shouldCalculateAverageTimeForMultipleCompletedOrders() {
        LocalDateTime createdAt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime completedAt1 = LocalDateTime.of(2023, 1, 1, 11, 0, 0); // 1 hour
        OrderDomain order1 = OrderDomain.restore(1L, "12345678909", "ABC1E67", OrderStatusType.FINALIZADA, "notes", 1L, 1L, 1L, createdAt1, null, completedAt1);

        LocalDateTime createdAt2 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime completedAt2 = LocalDateTime.of(2023, 1, 1, 12, 0, 0); // 2 hours
        OrderDomain order2 = OrderDomain.restore(2L, "11222333000181", "DEF2G89", OrderStatusType.FINALIZADA, "notes", 2L, 2L, 1L, createdAt2, null, completedAt2);

        List<OrderDomain> completedOrders = Arrays.asList(order1, order2);

        when(orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(completedOrders);

        AverageServiceExecutionTimeDTO time = useCase.execute();

        assertEquals(5400.0, time.seconds());
        assertEquals(90.0, time.minutes());
        assertEquals(1.5, time.hours());
        assertEquals("1:30:00", time.formattedTime());
    }

    @Test
    void shouldIgnoreOrdersWithoutCompletedAt() {
        LocalDateTime createdAt1 = LocalDateTime.of(2023, 1, 1, 10, 0, 0);
        LocalDateTime completedAt1 = LocalDateTime.of(2023, 1, 1, 11, 0, 0); // 1 hour
        OrderDomain order1 = OrderDomain.restore(1L, "12345678909", "ABC1E67", OrderStatusType.FINALIZADA, "notes", 1L, 1L, 1L, createdAt1, null, completedAt1);

        OrderDomain order2 = OrderDomain.restore(2L, "11222333000181", "DEF2G89", OrderStatusType.FINALIZADA, "notes", 2L, 2L, 2L, createdAt1, null, completedAt1); // No completedAt

        List<OrderDomain> completedOrders = Arrays.asList(order1, order2);

        when(orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(completedOrders);

        AverageServiceExecutionTimeDTO time = useCase.execute();

        assertEquals(3600.0, time.seconds());
        assertEquals(60.0, time.minutes());
        assertEquals(1.0, time.hours());
        assertEquals("1:00:00", time.formattedTime());
    }
}
