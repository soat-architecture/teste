package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @InjectMocks
    private FindOrderUseCase findOrderUseCase;

    @Test
    void shouldFindOrderById() {
        OrderDomain orderDomain = OrderDomain.create(
                "09242194913",
                "ABC-1234",
                "Teste",
                1L,
                1L
        );

        when(orderGateway.findById(1L)).thenReturn(Optional.of(orderDomain));
        OrderOutputDTO result = findOrderUseCase.execute(1L);

        assertNotNull(result);
    }

    @Test
    void testFindOrderById_ShouldThrowNotFound() {
        when(orderGateway.findById(1L)).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> findOrderUseCase.execute(1L)
        );

        assertEquals("order.not.found", thrown.getMessage());
    }
}
