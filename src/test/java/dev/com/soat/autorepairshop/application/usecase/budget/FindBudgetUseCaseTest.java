
package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.mapper.BudgetApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.BudgetDetailOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.mock.BudgetItemMock;
import dev.com.soat.autorepairshop.mock.BudgetMock;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para FindBudgetUseCase")
class FindBudgetUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private BudgetItemGateway budgetItemGateway;

    @InjectMocks
    private FindBudgetUseCase findBudgetUseCase;

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long BUDGET_ID = 1L;

    @Test
    @DisplayName("Deve encontrar orçamento com sucesso")
    void shouldFindBudgetSuccessfully() {
        // given
        OrderDomain order = ServiceOrderMock.buildDomain();
        BudgetDomain budget = BudgetMock.buildDomain();
        List<BudgetItemDomain> budgetItems = BudgetItemMock.buildListDomain();
        BudgetOutputDTO budgetOutputDTO = BudgetMock.buildOutputDTO();

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(SERVICE_ORDER_ID))
                .thenReturn(List.of(budget));
        when(budgetItemGateway.findByBudgetId(BUDGET_ID)).thenReturn(budgetItems);

        try (MockedStatic<BudgetApplicationMapper> mockedStatic = mockStatic(BudgetApplicationMapper.class)) {
            mockedStatic.when(() -> BudgetApplicationMapper.toOutputDTO(any(BudgetDomain.class)))
                    .thenReturn(budgetOutputDTO);

            // when
            BudgetDetailOutputDTO result = findBudgetUseCase.execute(SERVICE_ORDER_ID);

            // then
            assertNotNull(result);
            assertEquals(budgetOutputDTO, result.budget());
            assertNotNull(result.items());
        }
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem de serviço não for encontrada")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // given
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.empty());

        // when/then
        assertThrows(NotFoundException.class, () -> findBudgetUseCase.execute(SERVICE_ORDER_ID));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando não houver orçamentos")
    void shouldThrowNotFoundExceptionWhenNoBudgetsFound() {
        // given
        OrderDomain order = ServiceOrderMock.buildDomain();
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findAllByServiceOrderIdOrderByVersionDesc(SERVICE_ORDER_ID))
                .thenReturn(Collections.emptyList());

        // when/then
        assertThrows(NotFoundException.class, () -> findBudgetUseCase.execute(SERVICE_ORDER_ID));
    }
}