
package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.BudgetCalculatorHelper;
import dev.com.soat.autorepairshop.application.helper.BudgetItemAssemblerHelper;
import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.BudgetApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.BudgetGateway;
import dev.com.soat.autorepairshop.domain.gateway.BudgetItemGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import dev.com.soat.autorepairshop.mock.BudgetItemMock;
import dev.com.soat.autorepairshop.mock.BudgetMock;
import dev.com.soat.autorepairshop.mock.ServiceOrderMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para UpdateBudgetUseCase")
class UpdateBudgetUseCaseTest {

    @Mock
    private OrderGateway orderGateway;

    @Mock
    private BudgetGateway budgetGateway;

    @Mock
    private BudgetItemGateway budgetItemGateway;

    @Mock
    private BudgetCalculatorHelper budgetCalculatorHelper;

    @Mock
    private BudgetItemAssemblerHelper budgetItemAssemblerHelper;

    @Mock
    private UserValidationUtils userValidationUtils;

    @Mock
    private PartInventoryConsumptionHelper partInventoryConsumptionHelper;

    @InjectMocks
    private UpdateBudgetUseCase updateBudgetUseCase;

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long BUDGET_ID = 1L;
    private static final Long EMPLOYEE_ID = 1L;

    @Test
    @DisplayName("Deve atualizar orçamento com sucesso")
    void shouldUpdateBudgetSuccessfully() {
        // given
        BudgetInputDTO dto = createBudgetInputDTO();
        OrderDomain order = createOrderDomain(OrderStatusType.EM_DIAGNOSTICO);
        BudgetDomain baseBudget = createBudgetDomain();
        List<BudgetItemDomain> previousItems = createBudgetItems();
        List<BudgetItemDomain> newItems = createBudgetItems();
        BudgetDomain newBudget = createBudgetDomain();

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));
        when(budgetGateway.findById(BUDGET_ID)).thenReturn(baseBudget);
        when(budgetGateway.countByServiceOrderId(SERVICE_ORDER_ID)).thenReturn(1L);
        when(budgetItemGateway.findByBudgetId(BUDGET_ID)).thenReturn(previousItems);
        when(budgetCalculatorHelper.calculateTotal(anyList())).thenReturn(BigDecimal.valueOf(100));
        when(budgetItemAssemblerHelper.assemble(anyList(), anyLong())).thenReturn(newItems);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(newBudget);

        try (MockedStatic<BudgetApplicationMapper> mockedStatic = mockStatic(BudgetApplicationMapper.class)) {
            mockedStatic.when(() -> BudgetApplicationMapper.toDomainForNewVersionFrom(
                    ArgumentMatchers.any(BudgetDomain.class),
                    ArgumentMatchers.any(BudgetInputDTO.class),
                    ArgumentMatchers.any(BigDecimal.class),
                    ArgumentMatchers.anyInt()
            )).thenReturn(newBudget);

            // when
            updateBudgetUseCase.execute(dto);

            // then
            verify(budgetGateway).save(any(BudgetDomain.class));
            verify(budgetItemGateway).saveAll(newItems);
            verify(orderGateway).setActiveBudget(SERVICE_ORDER_ID, newBudget.getIdentifier());
        }
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando ordem não for encontrada")
    void shouldThrowNotFoundExceptionWhenOrderNotFound() {
        // given
        BudgetInputDTO dto = createBudgetInputDTO();
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.empty());

        // when/then
        assertThrows(NotFoundException.class, () -> updateBudgetUseCase.execute(dto));
    }

    @Test
    @DisplayName("Deve lançar ConflictException quando ordem não estiver em diagnóstico")
    void shouldThrowConflictExceptionWhenOrderNotInDiagnostic() {
        // given
        BudgetInputDTO dto = createBudgetInputDTO();
        OrderDomain order = createOrderDomain(OrderStatusType.RECEBIDA);
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));

        // when/then
        assertThrows(ConflictException.class, () -> updateBudgetUseCase.execute(dto));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando budgetId for nulo")
    void shouldThrowDomainExceptionWhenBudgetIdIsNull() {
        // given
        BudgetInputDTO dto = createBudgetInputDTO(null);
        OrderDomain order = createOrderDomain(OrderStatusType.EM_DIAGNOSTICO);
        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));

        // when/then
        assertThrows(DomainException.class, () -> updateBudgetUseCase.execute(dto));
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando tentar mudar o serviceOrderId")
    void shouldThrowIllegalArgumentExceptionWhenTryingToChangeServiceOrderId() {
        // given
        BudgetInputDTO dto = createBudgetInputDTO();
        OrderDomain order = createOrderDomain(OrderStatusType.EM_DIAGNOSTICO);

        when(orderGateway.findById(SERVICE_ORDER_ID)).thenReturn(Optional.of(order));

        // when/then
        assertThrows(NotFoundException.class, () -> updateBudgetUseCase.execute(dto));
    }

    private BudgetInputDTO createBudgetInputDTO() {
        return createBudgetInputDTO(BUDGET_ID);
    }

    private BudgetInputDTO createBudgetInputDTO(Long budgetId) {
        return new BudgetInputDTO(
                budgetId,
                SERVICE_ORDER_ID,
                EMPLOYEE_ID,
                "Notas",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 1.0))
        );
    }

    private OrderDomain createOrderDomain(final OrderStatusType status) {
        return ServiceOrderMock.buildDomain(status);
    }

    private BudgetDomain createBudgetDomain() {
        return BudgetMock.buildDomain();
    }

    private List<BudgetItemDomain> createBudgetItems() {
        return BudgetItemMock.buildListDomain();
    }
}