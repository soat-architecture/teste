
package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderMapper;
import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.OrderValidationUtils;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.mock.OrderHistoryMock;
import dev.com.soat.autorepairshop.mock.ServiceOrderMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para CreateOrderUseCase")
class CreateOrderUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @Mock
    private ClientValidationUtils clientValidationUtils;

    @Mock
    private VehicleValidationUtils vehicleValidationUtils;

    @Mock
    private UserValidationUtils userValidationUtils;

    @Mock
    private OrderValidationUtils orderValidationUtils;

    @InjectMocks
    private CreateOrderUseCase createOrderUseCase;

    private static final Long EMPLOYEE_ID = 1L;

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void shouldCreateOrderSuccessfully() {
        // given
        OrderInputDTO inputDTO = createOrderInputDTO();
        OrderDomain orderDomain = createOrderDomain();
        OrderHistoryDomain orderHistoryDomain = createOrderHistoryDomain();
        OrderOutputDTO expectedOutput = createOrderOutputDTO();

        try (MockedStatic<ApplicationOrderMapper> orderMapperMock = mockStatic(ApplicationOrderMapper.class);
             MockedStatic<ApplicationOrderHistoryMapper> historyMapperMock = mockStatic(ApplicationOrderHistoryMapper.class)) {

            orderMapperMock.when(() -> ApplicationOrderMapper.toDomain(any(OrderInputDTO.class)))
                    .thenReturn(orderDomain);
            orderMapperMock.when(() -> ApplicationOrderMapper.toDTO(any(OrderDomain.class)))
                    .thenReturn(expectedOutput);
            historyMapperMock.when(() -> ApplicationOrderHistoryMapper.map(any(OrderDomain.class)))
                    .thenReturn(orderHistoryDomain);

            when(orderGateway.save(any(OrderDomain.class))).thenReturn(orderDomain);

            // when
            OrderOutputDTO result = createOrderUseCase.execute(inputDTO);

            // then
            assertNotNull(result);
            assertEquals(expectedOutput, result);

            verify(clientValidationUtils).validateClientExistenceByDocument(orderDomain.getClientDocument().getValue());
            verify(userValidationUtils).validateUserExistenceById(EMPLOYEE_ID);
            verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(orderDomain.getVehicleLicensePlate().getValue());
            verify(orderValidationUtils).validateActiveOrderByVehicleLicensePlate(orderDomain.getVehicleLicensePlate().getValue());
            verify(orderGateway).save(any(OrderDomain.class));
            verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
        }
    }

    private OrderInputDTO createOrderInputDTO() {
        return ServiceOrderMock.buildInputDTO();
    }

    private OrderDomain createOrderDomain() {
        return ServiceOrderMock.buildDomain();
    }

    private OrderHistoryDomain createOrderHistoryDomain() {
        return OrderHistoryMock.buildDomain();
    }

    private OrderOutputDTO createOrderOutputDTO() {
        return ServiceOrderMock.buildOutputDTO();
    }
}