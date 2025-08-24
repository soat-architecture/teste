package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.Month;


class OrderHistoryDomainTest {

    @Test
    void testCreateOrderHistoryDomain() {
        OrderHistoryDomain result = OrderHistoryDomain.create(154L, OrderStatusType.RECEBIDA, "Teste");

        Assertions.assertNull(result.getIdentifier());
        Assertions.assertEquals(154L, result.getOrderId());
        Assertions.assertEquals(OrderStatusType.RECEBIDA, result.getOrderStatus());
        Assertions.assertEquals("Teste", result.getNotes());
        Assertions.assertNotNull(result.getCreatedAt());
    }

    @Test
    void testRestoreOrderHistoryDomain() {
        LocalDateTime changedAt = LocalDateTime.of(2025, Month.JULY, 26, 0, 0);

        OrderHistoryDomain result = OrderHistoryDomain.restore(12L,154L, OrderStatusType.RECEBIDA,
                "Teste", changedAt);

        Assertions.assertEquals(12L, result.getIdentifier());
        Assertions.assertEquals(154L, result.getOrderId());
        Assertions.assertEquals(OrderStatusType.RECEBIDA, result.getOrderStatus());
        Assertions.assertEquals("Teste", result.getNotes());
        Assertions.assertEquals(changedAt, result.getCreatedAt());
    }


}