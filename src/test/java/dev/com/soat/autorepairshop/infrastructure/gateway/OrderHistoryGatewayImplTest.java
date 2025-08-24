package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.infrastructure.repository.OrderHistoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderHistoryEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.OrderHistoryEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


class OrderHistoryGatewayImplTest {

    @Spy
    @InjectMocks
    private OrderHistoryGatewayImpl orderHistoryGateway;

    @Mock
    private OrderHistoryRepository orderHistoryRepository;

    private OrderHistoryEntity history;

    @BeforeEach
    void setUp() {
        history = new OrderHistoryEntity(1L,1L,OrderStatusType.RECEBIDA,"",LocalDateTime.now());

        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMethod() {
        OrderHistoryDomain orderHistoryDomain = OrderHistoryDomain.create(354L, OrderStatusType.RECEBIDA, "Teste");

        OrderHistoryEntity entity = OrderHistoryEntityMapper.toEntity(orderHistoryDomain);

        orderHistoryGateway.save(orderHistoryDomain);

        Mockito.verify(orderHistoryRepository).save(entity);
    }

    @Test
    void testFindAllByOrderId(){
        when(orderHistoryRepository.findAllByOrderId(anyLong())).thenReturn(List.of(history));
        List<OrderHistoryDomain> result = orderHistoryGateway.findAllByOrderId(1L);

        Mockito.verify(orderHistoryRepository).findAllByOrderId(1L);

        assertEquals(1, result.size());
    }


}