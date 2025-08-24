package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.infrastructure.repository.ClientRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.OrderRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.VehicleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.BudgetEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.OrderEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class OrderGatewayImplTest {

    @Spy
    @InjectMocks
    private OrderGatewayImpl orderGateway;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Test
    void testSaveMethod() {
        OrderDomain orderDomain = OrderDomain.create(
            "45997418000153",
            "ABC-1234",
            "Teste",
            32L,
            10L
        );

        OrderEntity entity = OrderEntityMapper.toEntity(orderDomain);
        entity.setIdentifier(1L);

        when(clientRepository.findByDocument(orderDomain.getClientDocument().unformat())).thenReturn(entity.getClient());
        when(vehicleRepository.findVehicleByLicensePlate(orderDomain.getVehicleLicensePlate().getValue())).thenReturn(entity.getVehicle());
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(entity);

        OrderDomain result = orderGateway.save(orderDomain);

        assertNotNull(result);
    }

    @Test
    void testFindActiveOrderByVehicleLicensePlateReturningDomain(){
        OrderDomain orderDomain = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        OrderEntity entity = OrderEntityMapper.toEntity(orderDomain);

        when(orderRepository.findOrderActiveByVehicleLicensePlate("ABC-1234")).thenReturn(entity);

        OrderDomain result = orderGateway.findActiveOrderByVehicleLicensePlate("ABC-1234");

        assertEquals(orderDomain, result);
        verify(orderRepository).findOrderActiveByVehicleLicensePlate("ABC-1234");
    }

    @Test
    void testFindActiveOrderByVehicleLicensePlateReturningNull(){
        when(orderRepository.findOrderActiveByVehicleLicensePlate("ABC-1234")).thenReturn(null);

        OrderDomain result = orderGateway.findActiveOrderByVehicleLicensePlate("ABC-1234");

        assertNull(result);
    }

    @Test
    void testFindAllOrders(){
        ClientEntity client = new ClientEntity(1L,
                "Teste",
                "45997418000153",
                "1234-5678",
                ClientStatus.ACTIVE,
                "email@email.com");
        VehicleEntity vehicle = new VehicleEntity(1L,
                "ABC-1234",
                "Fiat",
                "Uno com escada no teto",
                2000,
                "Lenda",
                "Suporte de escada",
                "",
                "Branco",
                "123456789",
                true);

        OrderEntity orderEntity = new OrderEntity(
                1L,
                client,
                vehicle,
                new BudgetEntity(),
                OrderStatusType.APROVADA,
                "Teste",
                1L,
                1L,
                LocalDateTime.now()
        );

        when(orderRepository.findAll()).thenReturn(List.of(orderEntity));

        List<OrderDomain> result = orderGateway.findAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testFindById() {
        OrderDomain orderDomain = OrderDomain.create("45997418000153", "ABC-1234", "Teste",
                32L, 10L);

        OrderEntity entity = OrderEntityMapper.toEntity(orderDomain);

        when(orderRepository.findById(1L)).thenReturn(java.util.Optional.of(entity));

        Optional<OrderDomain> result = orderGateway.findById(1L);


        assertEquals(orderDomain, result.get());
        verify(orderRepository).findById(1L);
    }

    @Test
    void findAllByStatusReturnsListOfOrdersWhenStatusExists() {
        OrderDomain order1 = OrderDomain.create("45997418000153", "ABC-1234", "Teste1", 32L, 10L);
        OrderDomain order2 = OrderDomain.create("45997418000153", "DEF-5678", "Teste2", 33L, 11L);

        OrderEntity entity1 = OrderEntityMapper.toEntity(order1);
        OrderEntity entity2 = OrderEntityMapper.toEntity(order2);

        when(orderRepository.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(List.of(entity1, entity2));

        List<OrderDomain> result = orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus());

        assertEquals(2, result.size());
        assertEquals(order1, result.get(0));
        assertEquals(order2, result.get(1));
        verify(orderRepository).findAllByStatus(OrderStatusType.returnFinishedStatus());
    }

    @Test
    void findAllByStatusReturnsEmptyListWhenNoOrdersExistForStatus() {
        when(orderRepository.findAllByStatus(OrderStatusType.returnFinishedStatus())).thenReturn(List.of());

        List<OrderDomain> result = orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus());

        assertEquals(0, result.size());
        verify(orderRepository).findAllByStatus(OrderStatusType.returnFinishedStatus());
    }

    @Test
    void setActiveBudget_whenRepositoryUpdatesRow_doesNotThrow() {
        // given
        Long orderId = 123L;
        Long budgetId = 456L;
        when(orderRepository.updateActiveBudget(orderId, budgetId)).thenReturn(1);

        // when / then
        assertDoesNotThrow(() -> orderGateway.setActiveBudget(orderId, budgetId));

        // verify
        verify(orderRepository).updateActiveBudget(orderId, budgetId);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void setActiveBudget_whenRepositoryReturnsZero_throwsIllegalState() {
        // given
        Long orderId = 123L;
        Long budgetId = 456L;
        when(orderRepository.updateActiveBudget(orderId, budgetId)).thenReturn(0);

        // when
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> orderGateway.setActiveBudget(orderId, budgetId)
        );

        // then
        assertEquals("Failed to set active budget: order not updated", ex.getMessage());
        verify(orderRepository).updateActiveBudget(orderId, budgetId);
        verifyNoMoreInteractions(orderRepository);
    }
}