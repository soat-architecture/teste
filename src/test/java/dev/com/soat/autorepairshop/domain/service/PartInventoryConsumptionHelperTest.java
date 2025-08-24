package dev.com.soat.autorepairshop.domain.service;

import dev.com.soat.autorepairshop.application.helper.PartInventoryConsumptionHelper;
import dev.com.soat.autorepairshop.application.mapper.InventoryMovementApplicationMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.InventoryMovementGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartInventoryGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartInventoryConsumptionHelperTest {

    @InjectMocks
    private PartInventoryConsumptionHelper service;

    @Mock
    private PartInventoryGateway inventory;

    @Mock
    private InventoryMovementGateway movementGateway;

    private final Long userId = 10L;
    private final Long soId = 20L;

    @Test
    void consumeFor_whenItemsNullOrEmpty_doesNothing() {
        assertDoesNotThrow(() -> service.consumeFor(null, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);

        var emptyList = List.<BudgetItemRequestDTO>of();
        assertDoesNotThrow(() -> service.consumeFor(emptyList, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void consumeFor_whenItemIsService_ignores() {
        var svcItem = new BudgetItemRequestDTO(1L, ItemTypeEnum.SERVICE, 2.0);
        var items = List.of(svcItem);
        assertDoesNotThrow(() -> service.consumeFor(items, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void consumeFor_whenQuantityIsNull_throwsIllegalArgument() {
        var partItem = new BudgetItemRequestDTO(2L, ItemTypeEnum.PART, null);
        var items = List.of(partItem);

        DomainException ex = assertThrows(DomainException.class,
                () -> service.consumeFor(items, userId, soId, "r"));

        assertEquals("part.quantity.required.id", ex.getMessage());
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void consumeFor_whenQuantityNotIntegerOrLeZero_throwsIllegalArgument() {
        var nonInteger = new BudgetItemRequestDTO(3L, ItemTypeEnum.PART, 1.5);
        var nonPositive = new BudgetItemRequestDTO(4L, ItemTypeEnum.PART, 0.0);
        var nonIntegerItems = List.of(nonInteger);
        var nonPositiveItems = List.of(nonPositive);

        DomainException ex1 = assertThrows(DomainException.class,
                () -> service.consumeFor(nonIntegerItems, userId, soId, "r"));
        assertEquals("part.quantity.must.be.integer.id", ex1.getMessage());

        DomainException ex2 = assertThrows(DomainException.class,
                () -> service.consumeFor(nonPositiveItems, userId, soId, "r"));
        assertEquals("part.quantity.must.be.integer.id", ex2.getMessage());

        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void consumeFor_whenInsufficientStock_throwsDomainException() {
        var item = new BudgetItemRequestDTO(5L, ItemTypeEnum.PART, 3.0);
        var items = List.of(item);
        when(inventory.getOnHand(5L)).thenReturn(2); // before < qty

        DomainException ex = assertThrows(DomainException.class,
                () -> service.consumeFor(items, userId, soId, "reason"));
        assertEquals("inventory.insufficient.part.id", ex.getMessage());

        verify(inventory).getOnHand(5L);
        verifyNoMoreInteractions(inventory);
        verifyNoInteractions(movementGateway);
    }

    @Test
    void consumeFor_happyPath_decreasesAndSavesMovement() {
        var item = new BudgetItemRequestDTO(6L, ItemTypeEnum.PART, 2.0);
        var items = List.of(item);
        when(inventory.getOnHand(6L)).thenReturn(5); // suficiente

        InventoryMovementDomain movementMock = mock(InventoryMovementDomain.class);
        try (MockedStatic<InventoryMovementApplicationMapper> mocked = mockStatic(InventoryMovementApplicationMapper.class)) {
            mocked.when(() -> InventoryMovementApplicationMapper.outboundSale(6L, userId, soId, 2, 5, "create"))
                    .thenReturn(movementMock);

            assertDoesNotThrow(() -> service.consumeFor(items, userId, soId, "create"));

            InOrder inOrder = inOrder(inventory, movementGateway);
            inOrder.verify(inventory).getOnHand(6L);
            inOrder.verify(inventory).decrease(6L, 2);
            mocked.verify(() -> InventoryMovementApplicationMapper.outboundSale(6L, userId, soId, 2, 5, "create"));
            inOrder.verify(movementGateway).save(movementMock);
            verifyNoMoreInteractions(inventory, movementGateway);
        }
    }

    @Test
    void restoreFor_whenItemsNullOrEmpty_doesNothing() {
        assertDoesNotThrow(() -> service.restoreFor(null, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);

        var emptyList = List.<BudgetItemDomain>of();
        assertDoesNotThrow(() -> service.restoreFor(emptyList, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void restoreFor_whenItemNotPart_ignores() {
        var dom = mock(BudgetItemDomain.class);
        when(dom.getItemType()).thenReturn(ItemTypeEnum.SERVICE);
        var items = List.of(dom);

        assertDoesNotThrow(() -> service.restoreFor(items, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void restoreFor_whenQuantityLeZero_ignores() {
        var dom = mock(BudgetItemDomain.class);
        when(dom.getItemType()).thenReturn(ItemTypeEnum.PART);
        when(dom.getQuantity()).thenReturn(0);
        var items = List.of(dom);

        assertDoesNotThrow(() -> service.restoreFor(items, userId, soId, "r"));
        verifyNoInteractions(inventory, movementGateway);
    }

    @Test
    void restoreFor_happyPath_increasesAndSavesMovement() {
        var dom = mock(BudgetItemDomain.class);
        when(dom.getItemType()).thenReturn(ItemTypeEnum.PART);
        when(dom.getQuantity()).thenReturn(3);
        when(dom.getPartId()).thenReturn(7L);
        var items = List.of(dom);

        when(inventory.getOnHand(7L)).thenReturn(10); // before

        InventoryMovementDomain movementMock = mock(InventoryMovementDomain.class);
        try (MockedStatic<InventoryMovementApplicationMapper> mocked = mockStatic(InventoryMovementApplicationMapper.class)) {
            mocked.when(() -> InventoryMovementApplicationMapper.adjustmentIncrease(7L, userId, soId, 3, 10, "restore.previous.budget.items"))
                    .thenReturn(movementMock);

            assertDoesNotThrow(() -> service.restoreFor(items, userId, soId, "ignored-reason")); // método define internamente seu reason fixo

            InOrder inOrder = inOrder(inventory, movementGateway);
            inOrder.verify(inventory).getOnHand(7L);
            inOrder.verify(inventory).increase(7L, 3);
            mocked.verify(() -> InventoryMovementApplicationMapper.adjustmentIncrease(7L, userId, soId, 3, 10, "restore.previous.budget.items"));
            inOrder.verify(movementGateway).save(movementMock);
            verifyNoMoreInteractions(inventory, movementGateway);
        }
    }
}