package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;

import java.time.LocalDateTime;

public class OrderHistoryMock {
    public static OrderHistoryDomain buildDomain(){
        return OrderHistoryDomain.restore(
                1L,
                1L,
                OrderStatusType.RECEBIDA,
                "notes",
                LocalDateTime.now()
        );
    }
}
