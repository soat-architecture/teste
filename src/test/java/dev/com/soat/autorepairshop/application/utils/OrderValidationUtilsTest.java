package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderValidationUtilsTest {

    @Spy
    @InjectMocks
    private OrderValidationUtils orderValidationUtils;

    @Mock
    private OrderGateway orderGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateActiveOrderByVehicleLicensePlateWithException(){
        String vehicleLicensePlate = "ABC-1234";

        OrderDomain orderDomainMock = Mockito.mock(OrderDomain.class);

        Mockito.when(orderGateway.findActiveOrderByVehicleLicensePlate(vehicleLicensePlate)).thenReturn(orderDomainMock);

        ConflictException conflictException = assertThrows(ConflictException.class, () -> orderValidationUtils.validateActiveOrderByVehicleLicensePlate(vehicleLicensePlate));
        Assertions.assertEquals("vehicle.has.active.order", conflictException.getMessage());
    }

    @Test
    void testValidateActiveOrderByVehicleLicensePlateWithoutException(){
        String vehicleLicensePlate = "ABC-1234";

        Mockito.when(orderGateway.findActiveOrderByVehicleLicensePlate(vehicleLicensePlate)).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> orderValidationUtils.validateActiveOrderByVehicleLicensePlate(vehicleLicensePlate));
    }

    @Test
    void testValidateOrderIsAssignedToEmployee_WhenOrderNotFound_ShouldThrowNotFoundException() {
        Long orderId = 1L;
        Long employeeId = 100L;

        Mockito.when(orderGateway.findById(orderId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId));

        Assertions.assertEquals("order.not.found", exception.getMessage());
    }

    @Test
    void testValidateOrderIsAssignedToEmployee_WhenOrderHasNoEmployeeAssigned_ShouldThrowDomainException() {
        Long orderId = 1L;
        Long employeeId = 100L;

        OrderDomain orderMock = Mockito.mock(OrderDomain.class);
        Mockito.when(orderMock.getEmployeeId()).thenReturn(null);
        Mockito.when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderMock));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId));

        Assertions.assertEquals("order.not.assigned.to.employee", exception.getMessage());
    }

    @Test
    void testValidateOrderIsAssignedToEmployee_WhenOrderAssignedToDifferentEmployee_ShouldThrowDomainException() {
        Long orderId = 1L;
        Long employeeId = 100L;
        Long differentEmployeeId = 200L;

        OrderDomain orderMock = Mockito.mock(OrderDomain.class);
        Mockito.when(orderMock.getEmployeeId()).thenReturn(differentEmployeeId);
        Mockito.when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderMock));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId));

        Assertions.assertEquals("order.not.assigned.to.employee", exception.getMessage());
    }

    @Test
    void testValidateOrderIsAssignedToEmployee_WhenOrderAssignedToCorrectEmployee_ShouldReturnOrder() {
        Long orderId = 1L;
        Long employeeId = 100L;

        OrderDomain orderMock = Mockito.mock(OrderDomain.class);
        Mockito.when(orderMock.getEmployeeId()).thenReturn(employeeId);
        Mockito.when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderMock));

        OrderDomain result = orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId);

        Assertions.assertEquals(orderMock, result);
        Assertions.assertEquals(employeeId, result.getEmployeeId());
    }

    @Test
    void testValidateOrderIsAssignedToEmployee_VerifyGatewayInteraction() {
        Long orderId = 1L;
        Long employeeId = 100L;

        OrderDomain orderMock = Mockito.mock(OrderDomain.class);
        Mockito.when(orderMock.getEmployeeId()).thenReturn(employeeId);
        Mockito.when(orderGateway.findById(orderId)).thenReturn(Optional.of(orderMock));

        orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId);

        Mockito.verify(orderGateway, Mockito.times(1)).findById(orderId);
        Mockito.verify(orderMock, Mockito.times(2)).getEmployeeId(); // Called twice: null check and equals check
    }
}