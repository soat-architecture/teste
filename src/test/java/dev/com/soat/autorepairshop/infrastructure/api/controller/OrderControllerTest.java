package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.order.*;
import dev.com.soat.autorepairshop.application.usecase.order.dto.AverageServiceExecutionTimeDTO;
import dev.com.soat.autorepairshop.application.usecase.order.dto.OrderDetailsOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.*;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import dev.com.soat.autorepairshop.mock.UserMock;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private GetOrdersUseCase getOrdersUseCase;

    @Mock
    private GetOrderStatusUseCase getOrderStatusUseCase;

    @Mock
    private AssignmentOrderUseCase assignmentOrderUseCase;

    @Mock
    private StartOrderUseCase startOrderUseCase;

    @Mock
    private FinishOrderUseCase finishOrderUseCase;

    @Mock
    private DeliveryOrderUseCase deliveryOrderUseCase;

    @Mock
    private ModifierEmployeeOrderUseCase modifierEmployeeOrderUseCase;

    @Mock
    private CalculateAverageServiceExecutionTimeUseCase calculateAverageServiceExecutionTimeUseCase;

    @Mock
    private FindOrderUseCase findOrderUseCase;

    @Mock
    private FindOrderDetailsUseCase findOrderDetailsUseCase;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
    }

    @Test
    void testListOrders() throws Exception {
        OrderOutputDTO orderOutputDTO = mock(OrderOutputDTO.class);

        when(getOrdersUseCase.execute(null, null)).thenReturn(List.of(orderOutputDTO));
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders"))
                .andExpect(status().isOk());
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve criar ordem de serviço com sucesso")
    void testCreate() throws Exception {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2025, 7, 25, 17, 0);
        final var outputDTO = new OrderOutputDTO(13L, "45997418000153", "ABC-1234",
                OrderStatusType.RECEBIDA.getDescription(), 23L, 10L, "Teste", createdAt, null, null);

        when(createOrderUseCase.execute(any(OrderInputDTO.class))).thenReturn(outputDTO);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "clientDocument": "45997418000153",
                                "vehicleLicensePlate": "ABC-1234",
                                "employeeIdentifier": 23,
                                "serviceId": 10,
                                "notes": "Teste"
                            }
                        """))
                .andExpect(status().isCreated());

        verify(createOrderUseCase).execute(any(OrderInputDTO.class));
    }

    @Test
    @DisplayName("Deve consultar status da ordem de serviço com sucesso")
    void testGetStatus() throws Exception {
        // Given
        final Long orderId = 1L;
        when(getOrderStatusUseCase.execute(orderId)).thenReturn(OrderStatusType.RECEBIDA);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/{id}/status", orderId))
                .andExpect(status().isOk());

        verify(getOrderStatusUseCase).execute(orderId);
    }

    @Test
    @DisplayName("Deve realizar atribuição de ordem de serviço com sucesso")
    void testAssignment() throws Exception {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final String token = "valid-jwt-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtTokenUtils.getIdentifierFromToken(token)).thenReturn(employeeId);
        doNothing().when(assignmentOrderUseCase).execute(orderId, employeeId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/assignment/" + orderId)
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(token);
        verify(assignmentOrderUseCase).execute(orderId, employeeId);
    }

    @Test
    @DisplayName("Deve iniciar ordem de serviço com sucesso")
    void testStart() throws Exception {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final String token = "valid-jwt-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtTokenUtils.getIdentifierFromToken(token)).thenReturn(employeeId);
        doNothing().when(startOrderUseCase).execute(orderId, employeeId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/start/" + orderId)
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(token);
        verify(startOrderUseCase).execute(orderId, employeeId);
    }

    @Test
    @DisplayName("Deve finalizar ordem de serviço com sucesso")
    void testFinish() throws Exception {
        // Given
        final Long orderId = 1L;
        final Long employeeId = 10L;
        final String token = "valid-jwt-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtTokenUtils.getIdentifierFromToken(token)).thenReturn(employeeId);
        doNothing().when(finishOrderUseCase).execute(orderId, employeeId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/finish/" + orderId)
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(token);
        verify(finishOrderUseCase).execute(orderId, employeeId);
    }

    @Test
    @DisplayName("Deve atualizar funcionário da ordem de serviço com sucesso")
    void testUpdateEmployee() throws Exception {
        // Given
        final Long orderId = 1L;
        final Long newEmployeeId = 20L;
        doNothing().when(modifierEmployeeOrderUseCase).execute(orderId, newEmployeeId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/orders/update-employee")
                        .header("orderId", orderId)
                        .header("newEmployeeId", newEmployeeId))
                .andExpect(status().isNoContent());

        verify(modifierEmployeeOrderUseCase).execute(orderId, newEmployeeId);
    }

    @Test
    @DisplayName("Deve falhar na atribuição quando token é inválido")
    void testAssignmentWithInvalidToken() throws Exception {
        // Given
        final Long orderId = 1L;
        final String invalidToken = "invalid-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(invalidToken);
        when(jwtTokenUtils.getIdentifierFromToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/assignment/" + orderId)
                        .cookie(new Cookie("token", invalidToken)))
                .andExpect(status().is5xxServerError());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(invalidToken);
        verify(assignmentOrderUseCase, never()).execute(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Deve falhar no início quando token é inválido")
    void testStartWithInvalidToken() throws Exception {
        // Given
        final long orderId = 1L;
        final String invalidToken = "invalid-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(invalidToken);
        when(jwtTokenUtils.getIdentifierFromToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/start/" + orderId)
                        .cookie(new Cookie("token", invalidToken)))
                .andExpect(status().is5xxServerError());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(invalidToken);
        verify(startOrderUseCase, never()).execute(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Deve falhar na finalização quando token é inválido")
    void testFinishWithInvalidToken() throws Exception {
        // Given
        final long orderId = 1L;
        final String invalidToken = "invalid-token";

        when(jwtTokenUtils.getTokenFromCookie(any(HttpServletRequest.class))).thenReturn(invalidToken);
        when(jwtTokenUtils.getIdentifierFromToken(invalidToken))
                .thenThrow(new RuntimeException("Invalid token"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders/finish/" + orderId)
                        .cookie(new Cookie("token", invalidToken)))
                .andExpect(status().is5xxServerError());

        verify(jwtTokenUtils).getTokenFromCookie(any(HttpServletRequest.class));
        verify(jwtTokenUtils).getIdentifierFromToken(invalidToken);
        verify(finishOrderUseCase, never()).execute(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Deve tratar exceção no use case de criação")
    void testCreateWithUseCaseException() throws Exception {
        // Given
        when(createOrderUseCase.execute(any(OrderInputDTO.class)))
                .thenThrow(new RuntimeException("Use case error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "clientDocument": "45997418000153",
                                "vehicleLicensePlate": "ABC-1234",
                                "employeeIdentifier": 23,
                                "serviceId": 10,
                                "notes": "Teste"
                            }
                        """))
                .andExpect(status().is5xxServerError());

        verify(createOrderUseCase).execute(any(OrderInputDTO.class));
    }

    @Test
    @DisplayName("Deve tratar exceção no use case de consulta de status")
    void testGetStatusWithUseCaseException() throws Exception {
        // Given
        final Long orderId = 1L;
        when(getOrderStatusUseCase.execute(orderId))
                .thenThrow(new RuntimeException("Use case error"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/{id}/status", orderId))
                .andExpect(status().is5xxServerError());

        verify(getOrderStatusUseCase).execute(orderId);
    }

    @Test
    void getAverageServiceExecutionTimeReturnsValidResponseWhenDataExists() throws Exception {
        AverageServiceExecutionTimeDTO dto = new AverageServiceExecutionTimeDTO(3600.0, 60.0, 1.0, "1:00:00");
        when(calculateAverageServiceExecutionTimeUseCase.execute()).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/average-service-execution-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total_seconds").value(3600.0))
                .andExpect(jsonPath("$.data.total_minutes").value(60.0))
                .andExpect(jsonPath("$.data.total_hours").value(1.0))
                .andExpect(jsonPath("$.data.formatted_time").value("1:00:00"));
    }

    @Test
    void getAverageServiceExecutionTimeReturnsZeroValuesWhenNoDataExists() throws Exception {
        AverageServiceExecutionTimeDTO dto = AverageServiceExecutionTimeDTO.empty();
        Mockito.when(calculateAverageServiceExecutionTimeUseCase.execute()).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/average-service-execution-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total_seconds").value(0.0))
                .andExpect(jsonPath("$.data.total_minutes").value(0.0))
                .andExpect(jsonPath("$.data.total_hours").value(0.0))
                .andExpect(jsonPath("$.data.formatted_time").value("00:00:00"));
    }

    @Test
    void getAverageServiceExecutionTimeReturnsInternalServerErrorOnException() throws Exception {
        Mockito.when(calculateAverageServiceExecutionTimeUseCase.execute()).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/average-service-execution-time")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testFindOrder() throws Exception {
        OrderOutputDTO orderOutputDTO = mock(OrderOutputDTO.class);

        when(findOrderUseCase.execute(1L)).thenReturn(orderOutputDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/" + 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testFindOrderDetails() throws Exception {
        OrderDetailsOutputDTO outputDTO = new OrderDetailsOutputDTO(
                OrderDomain.restore(1L,"336.944.240-08","ABC-1234",OrderStatusType.EM_DIAGNOSTICO,"1234",1L,1L,1L,LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()),
                new ClientDomain(1L, "John Doe", "336.944.240-08", "(11) 99999-9999", "john.doe@email.com", ClientStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()),
                UserMock.buildMockDomain(),
                VehicleDomain.restore(1L,"ABC-1234", "Fiat", "Uno", 2000, "Carro", "Hatch", "", "Branco", "45997418000153", LocalDateTime.now(), LocalDateTime.now(),true),
                BudgetDomain.restore(1L,1L,1, BigDecimal.valueOf(100L), BudgetStatus.APPROVED,"", LocalDateTime.now(), LocalDateTime.now()),
                List.of(BudgetItemDomain.restore(1L, 1L, ItemTypeEnum.PART,1L,1L,100,BigDecimal.valueOf(100L),LocalDateTime.now())),
                List.of(OrderHistoryDomain.restore(1L,1L,OrderStatusType.RECEBIDA,"",LocalDateTime.now()))
                );

        when(findOrderDetailsUseCase.execute(1L)).thenReturn(outputDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/orders/1/details"))
                .andExpect(status().isOk());
    }
}