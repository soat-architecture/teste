package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.usecase.order.dto.OrderDetailsOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.*;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.*;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindOrderDetailsUseCaseTest {
    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private BudgetItemGateway budgetItemGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private FindOrderDetailsUseCase findOrderDetailsUseCase;

    private OrderDomain orderDomain;
    private ClientDomain clientDomain;
    private VehicleDomain vehicleDomain;
    private BudgetDomain budgetDomain;
    private UserDomain userDomain;
    private List<BudgetItemDomain> budgetItems = new ArrayList<>();
    private List<OrderHistoryDomain> orderHistory = new ArrayList<>();

    @BeforeEach
    void setUp(){
        orderDomain = OrderDomain.restore(1L,"336.944.240-08","ABC-1234", OrderStatusType.EM_DIAGNOSTICO,"1234",1L,1L,1L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
        clientDomain = new ClientDomain(1L, "John Doe", "336.944.240-08", "(11) 99999-9999", "john.doe@email.com", ClientStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        userDomain = UserMock.buildMockDomain();
        vehicleDomain = VehicleDomain.restore(1L,"ABC-1234", "Fiat", "Uno", 2000, "Carro", "Hatch", "", "Branco", "45997418000153", LocalDateTime.now(), LocalDateTime.now(),true);
        budgetDomain = BudgetDomain.restore(1L,1L,1, BigDecimal.valueOf(100L), BudgetStatus.APPROVED,"", LocalDateTime.now(), LocalDateTime.now());
        budgetItems = List.of(BudgetItemDomain.restore(1L, 1L, ItemTypeEnum.PART,1L,1L,100,BigDecimal.valueOf(100L),LocalDateTime.now()));
        orderHistory =List.of(OrderHistoryDomain.restore(1L,1L,OrderStatusType.RECEBIDA,"",LocalDateTime.now()));
    }

    @Test
    void whenFindOrderDetails_shouldReturnDetailedOrder(){
        when(orderGateway.findById(anyLong())).thenReturn(Optional.of(orderDomain));
        when(clientGateway.findByDocument(anyString())).thenReturn(clientDomain);
        when(vehicleGateway.findVehicleByLicensePlate(anyString())).thenReturn(Optional.of(vehicleDomain));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(anyLong())).thenReturn(List.of(budgetDomain));
        when(budgetItemGateway.findByBudgetId(anyLong())).thenReturn(budgetItems);
        when(orderHistoryGateway.findAllByOrderId(anyLong())).thenReturn(orderHistory);
        when(userGateway.findEmployeeById(anyLong())).thenReturn(Optional.of(userDomain));

        OrderDetailsOutputDTO result = findOrderDetailsUseCase.execute(1L);
        assertNotNull(result);
    }

    @Test
    void whenFindOrderDetailsWithoutBudget_shouldReturnDetailedOrder(){
        when(orderGateway.findById(anyLong())).thenReturn(Optional.of(orderDomain));
        when(clientGateway.findByDocument(anyString())).thenReturn(clientDomain);
        when(vehicleGateway.findVehicleByLicensePlate(anyString())).thenReturn(Optional.of(vehicleDomain));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());
        when(orderHistoryGateway.findAllByOrderId(anyLong())).thenReturn(orderHistory);
        when(userGateway.findEmployeeById(anyLong())).thenReturn(Optional.of(userDomain));

        OrderDetailsOutputDTO result = findOrderDetailsUseCase.execute(1L);
        assertNotNull(result);
    }

    @Test
    void whenFindOrderDetailsWithoutAssignedEmployee_shouldReturnDetailedOrder(){
        OrderDomain orderWithoutEmployee = OrderDomain.restore(1L,"336.944.240-08","ABC-1234", OrderStatusType.EM_DIAGNOSTICO,"1234",null,1L,1L, LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        when(orderGateway.findById(anyLong())).thenReturn(Optional.of(orderWithoutEmployee));
        when(clientGateway.findByDocument(anyString())).thenReturn(clientDomain);
        when(vehicleGateway.findVehicleByLicensePlate(anyString())).thenReturn(Optional.of(vehicleDomain));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(anyLong())).thenReturn(Collections.emptyList());
        when(orderHistoryGateway.findAllByOrderId(anyLong())).thenReturn(orderHistory);

        OrderDetailsOutputDTO result = findOrderDetailsUseCase.execute(1L);
        assertNotNull(result);
    }

    @Test
    void whenFindOrderDetails_shouldNotFindOrder(){
        when(orderGateway.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> findOrderDetailsUseCase.execute(1L)
        );

        assertEquals("Order not found", thrown.getMessage());
    }

    @Test
    void whenFindOrderDetails_shouldNotFindVehicle(){
        when(orderGateway.findById(anyLong())).thenReturn(Optional.of(orderDomain));
        when(clientGateway.findByDocument(anyString())).thenReturn(clientDomain);
        when(vehicleGateway.findVehicleByLicensePlate(anyString())).thenReturn(Optional.empty());


        NotFoundException thrown = assertThrows(
                NotFoundException.class,
                () -> findOrderDetailsUseCase.execute(1L)
        );

        assertEquals("Vehicle not found",thrown.getMessage());
    }
}
