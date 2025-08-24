package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.*;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.mock.ServiceMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

class PrepareBudgetEmailUseCaseTest {

    @Spy
    @InjectMocks
    private PrepareBudgetEmailUseCase prepareBudgetEmailUseCase;

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private BudgetItemGateway budgetItemGateway;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @Mock
    private PartGateway partGateway;

    @Mock
    private ServiceGateway serviceGateway;

    @Mock
    private ClientValidationUtils clientValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteMethodBudgetNotFound() {
        Long serviceOrderId = 14L;

        Mockito.when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(serviceOrderId)).thenReturn(new ArrayList<>());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> prepareBudgetEmailUseCase.execute(serviceOrderId));

        Assertions.assertEquals("budget.not.found", notFoundException.getMessage());
    }

    @Test
    void testExecuteMethodOrderNotFound() {
        Long serviceOrderId = 14L;

        BudgetDomain budgetDomain = BudgetDomain.create(serviceOrderId, new BigDecimal("400.0"), "");

        Mockito.when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(serviceOrderId)).thenReturn(List.of(budgetDomain));
        Mockito.when(orderGateway.findById(serviceOrderId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> prepareBudgetEmailUseCase.execute(serviceOrderId));

        Assertions.assertEquals("order.not.found", notFoundException.getMessage());
    }

    @Test
    void testExecuteMethodSuccess() {
        Long serviceOrderId = 14L;
        Long budgetId = 3L;

        BudgetDomain budgetDomain = BudgetDomain.restore(budgetId, serviceOrderId, 1,  new BigDecimal("400.0"), BudgetStatus.PENDING_APPROVAL, "", LocalDateTime.now(), LocalDateTime.now());
        OrderDomain orderDomain = OrderDomain.create("45997418000153", "ABC-1234", "", 45L, serviceOrderId);
        ClientDomain clientDomain = new ClientDomain(4L, "Cliente Teste", "45997418000153", "(15) 2443-4456", "john.doe@example.com", ClientStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());

        BudgetItemDomain serviceItemDomain = new BudgetItemDomain(1L, budgetId, ItemTypeEnum.SERVICE, 2L, 1L,1, new BigDecimal("100.0"), LocalDateTime.now());
        BudgetItemDomain partItemDomain = new BudgetItemDomain(2L, budgetId, ItemTypeEnum.PART, 2L, 1L,1, new BigDecimal("100.0"), LocalDateTime.now());
        List<BudgetItemDomain> budgetItemsDomain = List.of(serviceItemDomain, partItemDomain);

        PartDomain partDomain = new PartDomain(1L, "Troca de Óleo", "", "", "", new BigDecimal("100.0"), new BigDecimal("80.0"), true, LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(serviceOrderId)).thenReturn(List.of(budgetDomain));
        Mockito.when(orderGateway.findById(serviceOrderId)).thenReturn(Optional.of(orderDomain));
        Mockito.when(clientValidationUtils.validateClientExistenceByDocument(orderDomain.getClientDocument().getValue())).thenReturn(clientDomain);
        Mockito.when(budgetItemGateway.findBudgetItemsByBudgetId(budgetDomain.getIdentifier())).thenReturn(budgetItemsDomain);
        Mockito.when(partGateway.findById(partItemDomain.getPartId())).thenReturn(partDomain);
        Mockito.when(serviceGateway.findById(serviceItemDomain.getServiceId())).thenReturn(ServiceMock.buildDomain());
        doNothing().when(orderHistoryGateway).save(any(OrderHistoryDomain.class));

        Assertions.assertEquals(OrderStatusType.RECEBIDA, orderDomain.getStatus());

        prepareBudgetEmailUseCase.execute(serviceOrderId);

        Assertions.assertEquals(OrderStatusType.AGUARDANDO_APROVACAO, orderDomain.getStatus());
        Mockito.verify(orderGateway).save(orderDomain);
    }

}