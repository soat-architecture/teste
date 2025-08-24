
package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.mock.ServiceOrderMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para ApproveBudgetUseCase")
class ApproveBudgetUseCaseTest {

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private OrderHistoryGateway orderHistoryGateway;

    @InjectMocks
    private ApproveBudgetUseCase approveBudgetUseCase;

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long BUDGET_ID = 1L;
    private OrderDomain orderDomain;
    private BudgetDomain budgetDomain;

    @BeforeEach
    void setUp() {
        orderDomain = ServiceOrderMock.buildDomain();

        budgetDomain = BudgetDomain.create(
                SERVICE_ORDER_ID,
                BigDecimal.valueOf(100.0),
                "Notas de teste"
        );
    }

    @Test
    @DisplayName("Deve aprovar orçamento com sucesso")
    void shouldApproveBudgetSuccessfully() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(orderDomain));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(budgetDomain);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(budgetDomain);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(orderDomain);

        // when
        approveBudgetUseCase.execute(SERVICE_ORDER_ID);

        // then
        verify(budgetGateway).save(any(BudgetDomain.class));
        verify(orderGateway).save(any(OrderDomain.class));
        verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem de serviço não for encontrada")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.empty());

        // when/then
        assertThrows(NotFoundException.class, () -> approveBudgetUseCase.execute(SERVICE_ORDER_ID));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando orçamento não for encontrado")
    void shouldThrowNotFoundExceptionWhenBudgetNotFound() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(orderDomain));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(null);

        // when/then
        assertThrows(NotFoundException.class, () -> approveBudgetUseCase.execute(SERVICE_ORDER_ID));
    }

    @Test
    @DisplayName("Deve salvar histórico da ordem após aprovação")
    void shouldSaveOrderHistoryAfterApproval() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(orderDomain));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(budgetDomain);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(budgetDomain);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(orderDomain);

        // when
        approveBudgetUseCase.execute(SERVICE_ORDER_ID);

        // then
        verify(orderHistoryGateway).save(any(OrderHistoryDomain.class));
    }

    @Test
    @DisplayName("Deve atualizar status do orçamento e da ordem")
    void shouldUpdateBudgetAndOrderStatus() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(orderDomain));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(budgetDomain);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(budgetDomain);
        when(orderGateway.save(any(OrderDomain.class))).thenReturn(orderDomain);

        // when
        approveBudgetUseCase.execute(SERVICE_ORDER_ID);

        // then
        verify(budgetGateway).save(any(BudgetDomain.class));
        verify(orderGateway).save(any(OrderDomain.class));
    }
}