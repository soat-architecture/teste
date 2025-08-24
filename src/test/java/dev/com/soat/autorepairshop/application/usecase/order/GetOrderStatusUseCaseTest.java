package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderStatusUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private GetOrderStatusUseCase getOrderStatusUseCase;

    @Test
    void execute_shouldReturnOrderStatus_whenOrderExists() {
        Long orderId = 1L;
        OrderDomain orderDomain = OrderDomain.restore(1L, "09242194913", "ABC1D34", OrderStatusType.APROVADA, "notes", 1L, 1L, 1L, null, null, null);
        when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderDomain));

        OrderStatusType result = getOrderStatusUseCase.execute(orderId);

        assertEquals(OrderStatusType.APROVADA, result);
        verify(orderGateway, times(1)).findById(orderId);
    }

    @Test
    void execute_shouldThrowNotFoundException_whenOrderDoesNotExist() {
        Long orderId = 1L;
        when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> getOrderStatusUseCase.execute(orderId));
        verify(orderGateway, times(1)).findById(orderId);
    }
}
