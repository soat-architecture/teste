package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusResponseTest {

    @Test
    void testOrderStatusResponse() {
        OrderStatusResponse response = new OrderStatusResponse(OrderStatusType.APROVADA);

        assertEquals(OrderStatusType.APROVADA, response.status());

        OrderStatusResponse response1 = new OrderStatusResponse(OrderStatusType.EM_DIAGNOSTICO);
        assertEquals(OrderStatusType.EM_DIAGNOSTICO, response1.status());
    }


}