package dev.com.soat.autorepairshop.application.usecase.budget;

import dev.com.soat.autorepairshop.application.helper.BudgetCalculatorHelper;
import dev.com.soat.autorepairshop.application.helper.BudgetItemAssemblerHelper;
import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateBudgetUseCaseTest {

    private CreateBudgetUseCase useCase;

    private OrderGateway orderGateway;
    private BudgetGateway budgetGateway;
    private BudgetItemGateway budgetItemGateway;
    private BudgetCalculatorHelper budgetCalculatorHelper;
    private BudgetItemAssemblerHelper budgetItemAssemblerHelper;
    private UserValidationUtils userValidationUtils;
    private PartInventoryConsumptionHelper partInventoryConsumptionHelper;

    @BeforeEach
    void setUp() {
        orderGateway = mock(OrderGateway.class);
        budgetGateway = mock(BudgetGateway.class);
        budgetItemGateway = mock(BudgetItemGateway.class);
        budgetCalculatorHelper = mock(BudgetCalculatorHelper.class);
        budgetItemAssemblerHelper = mock(BudgetItemAssemblerHelper.class);
        userValidationUtils = mock(UserValidationUtils.class);
        partInventoryConsumptionHelper = mock(PartInventoryConsumptionHelper.class);

        useCase = new CreateBudgetUseCase(
                orderGateway,
                budgetGateway,
                budgetItemGateway,
                budgetCalculatorHelper,
                budgetItemAssemblerHelper,
                partInventoryConsumptionHelper,
                userValidationUtils
        );
    }

    @Test
    void execute_withValidInput_createsVersion1_persistsItems_linksBudget_andConsumesStock() {
        // given
        var dto = new BudgetInputDTO(
                null,                // budgetId (create)
                1L,                  // serviceOrderId
                2L,                  // employeeId
                "note",              // notes
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0))
        );

        // usuário válido
        doReturn(mock(dev.com.soat.autorepairshop.domain.entity.UserDomain.class))
                .when(userValidationUtils).validateUserExistenceById(2L);

        // não existe budget anterior para a OS
        when(budgetGateway.countByServiceOrderId(1L)).thenReturn(0L);

        // OS existente e em diagnóstico
        OrderDomain order = OrderDomain.create("07975278009", "ABC-1234", "", 1L, 1L);
        order.changeStatus(OrderStatusType.EM_DIAGNOSTICO);
        when(orderGateway.findById(1L)).thenReturn(Optional.of(order));

        // cálculo do total
        when(budgetCalculatorHelper.calculateTotal(dto.items()))
                .thenReturn(new BigDecimal("123.45"));

        // salvar orçamento -> retorna com ID 10
        BudgetDomain savedBudget = mock(BudgetDomain.class);
        when(savedBudget.getIdentifier()).thenReturn(10L);
        when(budgetGateway.save(any(BudgetDomain.class))).thenReturn(savedBudget);

        // montar e salvar itens
        List<BudgetItemDomain> assembledItems = List.of(mock(BudgetItemDomain.class));
        when(budgetItemAssemblerHelper.assemble(dto.items(), 10L)).thenReturn(assembledItems);
        when(budgetItemGateway.saveAll(assembledItems)).thenReturn(assembledItems);

        // when / then
        assertDoesNotThrow(() -> useCase.execute(dto));

        // verifies
        verify(userValidationUtils).validateUserExistenceById(2L);
        verify(budgetGateway).countByServiceOrderId(1L);
        verify(orderGateway).findById(1L);
        verify(budgetCalculatorHelper).calculateTotal(dto.items());
        verify(budgetGateway).save(any(BudgetDomain.class));
        verify(budgetItemAssemblerHelper).assemble(dto.items(), 10L);
        verify(budgetItemGateway).saveAll(assembledItems);
        verify(partInventoryConsumptionHelper).consumeFor(dto.items(), 2L, 1L, "BUDGET_CREATE 10");
    }

    @Test
    void execute_whenExistingBudgetForServiceOrder_throwsConflict_andDoesNotPersist() {
        // given
        var dto = new BudgetInputDTO(
                null, 1L, 2L, "note",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0))
        );

        doReturn(mock(dev.com.soat.autorepairshop.domain.entity.UserDomain.class))
                .when(userValidationUtils).validateUserExistenceById(2L);
        when(budgetGateway.countByServiceOrderId(1L)).thenReturn(1L); // já existe

        // when
        ConflictException ex = assertThrows(ConflictException.class, () -> useCase.execute(dto));

        // then
        assertEquals("budget.already.exists.for.service.order", ex.getMessage());
        verify(userValidationUtils).validateUserExistenceById(2L);
        verify(budgetGateway).countByServiceOrderId(1L);
        verifyNoInteractions(orderGateway, budgetCalculatorHelper, budgetItemAssemblerHelper, budgetItemGateway, partInventoryConsumptionHelper);
        verifyNoMoreInteractions(budgetGateway, userValidationUtils);
    }

    @Test
    void execute_whenUserNotFound_throwsNotFound_andDoesNotTouchBudget() {
        // given
        var dto = new BudgetInputDTO(
                null, 1L, 999L, "note",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0))
        );

        doThrow(new NotFoundException("user.does.not.exists"))
                .when(userValidationUtils).validateUserExistenceById(999L);

        // when
        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute(dto));

        // then
        assertEquals("user.does.not.exists", ex.getMessage());
        verify(userValidationUtils).validateUserExistenceById(999L);
        verifyNoInteractions(budgetGateway, orderGateway, budgetCalculatorHelper, budgetItemAssemblerHelper, budgetItemGateway, partInventoryConsumptionHelper);
        verifyNoMoreInteractions(userValidationUtils);
    }

    @Test
    void execute_whenServiceOrderNotFound_throwsNotFound() {
        // given
        var dto = new BudgetInputDTO(
                null, 1L, 2L, "",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0))
        );

        doReturn(mock(dev.com.soat.autorepairshop.domain.entity.UserDomain.class))
                .when(userValidationUtils).validateUserExistenceById(2L);
        when(budgetGateway.countByServiceOrderId(1L)).thenReturn(0L);
        when(orderGateway.findById(1L)).thenReturn(Optional.empty());

        // when
        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute(dto));

        // then (mensagem real do use case)
        assertEquals("service.order.not.found", ex.getMessage());
        verify(userValidationUtils).validateUserExistenceById(2L);
        verify(budgetGateway).countByServiceOrderId(1L);
        verify(orderGateway).findById(1L);
        verifyNoInteractions(budgetCalculatorHelper, budgetItemAssemblerHelper, budgetItemGateway, partInventoryConsumptionHelper);
        verifyNoMoreInteractions(userValidationUtils, budgetGateway, orderGateway);
    }

    @Test
    void execute_whenOrderStatusInvalid_throwsDomainException() {
        // given
        var dto = new BudgetInputDTO(
                null, 1L, 2L, "",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0))
        );

        doReturn(mock(dev.com.soat.autorepairshop.domain.entity.UserDomain.class))
                .when(userValidationUtils).validateUserExistenceById(2L);
        when(budgetGateway.countByServiceOrderId(1L)).thenReturn(0L);

        OrderDomain order = OrderDomain.create("07975278009", "ABC-1234", "", 1L, 1L);
        order.changeStatus(OrderStatusType.RECEBIDA); // inválido para o caso
        when(orderGateway.findById(1L)).thenReturn(Optional.of(order));

        // when
        DomainException ex = assertThrows(DomainException.class, () -> useCase.execute(dto));

        // then (mensagem real do use case)
        assertEquals("order.status.must.be.diagnostic", ex.getMessage());
        verify(userValidationUtils).validateUserExistenceById(2L);
        verify(budgetGateway).countByServiceOrderId(1L);
        verify(orderGateway).findById(1L);
        verifyNoInteractions(budgetCalculatorHelper, budgetItemAssemblerHelper, budgetItemGateway, partInventoryConsumptionHelper);
        verifyNoMoreInteractions(userValidationUtils, budgetGateway, orderGateway);
    }
}
