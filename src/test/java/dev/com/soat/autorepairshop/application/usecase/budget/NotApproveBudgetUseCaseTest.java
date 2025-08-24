
package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.mock.BudgetItemMock;
import dev.com.soat.autorepairshop.mock.BudgetMock;
import dev.com.soat.autorepairshop.mock.OrderHistoryMock;
import dev.com.soat.autorepairshop.mock.ServiceOrderMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para NotApproveBudgetUseCase")
class NotApproveBudgetUseCaseTest {

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private BudgetItemGateway budgetItemGateway;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @Mock
    private PartInventoryConsumptionHelper partInventoryConsumptionHelper;

    @InjectMocks
    private NotApproveBudgetUseCase notApproveBudgetUseCase;

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long BUDGET_ID = 1L;

    @Test
    @DisplayName("Deve não aprovar orçamento com sucesso")
    void shouldNotApproveBudgetSuccessfully() {
        // given
        OrderDomain order = createOrderDomain();
        BudgetDomain budget = createBudgetDomain();
        List<BudgetItemDomain> budgetItems = createBudgetItems();
        OrderHistoryDomain orderHistory = createOrderHistoryDomain();

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(SERVICE_ORDER_ID))
                .thenReturn(List.of(budget));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(budget);
        when(budgetItemGateway.findBudgetItemsByBudgetId(BUDGET_ID)).thenReturn(budgetItems);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(budget);

        try (MockedStatic<ApplicationOrderHistoryMapper> mockedStatic = mockStatic(ApplicationOrderHistoryMapper.class)) {
            mockedStatic.when(() -> ApplicationOrderHistoryMapper.map(any(OrderDomain.class)))
                    .thenReturn(orderHistory);

            // when
            notApproveBudgetUseCase.execute(SERVICE_ORDER_ID);

            // then
            verify(budgetGateway).save(any(BudgetDomain.class));
            verify(orderGateway).save(any(OrderDomain.class));
            verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
            verify(partInventoryConsumptionHelper).restoreFor(
                    anyList(),
                    isNull(),
                    eq(SERVICE_ORDER_ID),
                    eq("BUDGET_NOT_APPROVED")
            );
        }
    }

    @Test
    @DisplayName("Deve não aprovar múltiplos orçamentos com sucesso")
    void shouldNotApproveMultipleBudgetsSuccessfully() {
        // given
        OrderDomain order = createOrderDomain();
        BudgetDomain budget1 = createBudgetDomain();
        BudgetDomain budget2 = createBudgetDomain();
        List<BudgetItemDomain> budgetItems = createBudgetItems();
        OrderHistoryDomain orderHistory = createOrderHistoryDomain();

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(SERVICE_ORDER_ID))
                .thenReturn(List.of(budget1, budget2));
        when(budgetGateway.findById(anyLong())).thenReturn(budget1);
        when(budgetItemGateway.findBudgetItemsByBudgetId(anyLong())).thenReturn(budgetItems);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(budget1);

        try (MockedStatic<ApplicationOrderHistoryMapper> mockedStatic = mockStatic(ApplicationOrderHistoryMapper.class)) {
            mockedStatic.when(() -> ApplicationOrderHistoryMapper.map(any(OrderDomain.class)))
                    .thenReturn(orderHistory);

            // when
            notApproveBudgetUseCase.execute(SERVICE_ORDER_ID);

            // then
            verify(budgetGateway, times(2)).save(any(BudgetDomain.class));
            verify(orderGateway).save(any(OrderDomain.class));
            verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
            verify(partInventoryConsumptionHelper, times(2)).restoreFor(
                    anyList(),
                    isNull(),
                    eq(SERVICE_ORDER_ID),
                    eq("BUDGET_NOT_APPROVED")
            );
        }
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem de serviço não for encontrada")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.empty());

        // when/then
        assertThrows(NotFoundException.class, () -> notApproveBudgetUseCase.execute(SERVICE_ORDER_ID));
    }

    @Test
    @DisplayName("Deve processar com sucesso quando não houver orçamentos")
    void shouldProcessSuccessfullyWhenNoBudgetsFound() {
        // given
        OrderDomain order = createOrderDomain();
        OrderHistoryDomain orderHistory = createOrderHistoryDomain();

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(SERVICE_ORDER_ID))
                .thenReturn(Collections.emptyList());
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(order);

        try (MockedStatic<ApplicationOrderHistoryMapper> mockedStatic = mockStatic(ApplicationOrderHistoryMapper.class)) {
            mockedStatic.when(() -> ApplicationOrderHistoryMapper.map(any(OrderDomain.class)))
                    .thenReturn(orderHistory);

            // when
            notApproveBudgetUseCase.execute(SERVICE_ORDER_ID);

            // then
            verify(orderGateway).save(any(OrderDomain.class));
            verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
        }
    }

    private OrderDomain createOrderDomain() {
        return ServiceOrderMock.buildDomain();
    }

    private BudgetDomain createBudgetDomain() {
        return BudgetMock.buildDomain();
    }

    private List<BudgetItemDomain> createBudgetItems() {
        return BudgetItemMock.buildListDomain();
    }

    private OrderHistoryDomain createOrderHistoryDomain() {
        return OrderHistoryMock.buildDomain();
    }
}