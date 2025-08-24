package dev.com.soat.autorepairshop.domain.service;

import dev.com.soat.autorepairshop.application.helper.BudgetCalculatorHelper;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.PartInventoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetCalculatorHelperTest {

    private PartGateway partGateway;
    private BudgetCalculatorHelper service;
    private ServiceGateway serviceGateway;
    private PartInventoryGateway partInventoryGateway;

    @BeforeEach
    void setUp() {
        partGateway = mock(PartGateway.class);
        serviceGateway = mock(ServiceGateway.class);
        partInventoryGateway = mock(PartInventoryGateway.class);
        service = new BudgetCalculatorHelper(partGateway, serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenListIsNull_returnsZero() {
        BigDecimal total = service.calculateTotal(null);
        assertEquals(0, total.compareTo(BigDecimal.ZERO));
        verifyNoInteractions(partGateway, serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenListIsEmpty_returnsZero() {
        BigDecimal total = service.calculateTotal(List.of());
        assertEquals(0, total.compareTo(BigDecimal.ZERO));
        verifyNoInteractions(partGateway, serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_withSinglePart_sumsQuantityTimesUnitPrice_andChecksStock() {
        var item = new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0);

        PartDomain part = mock(PartDomain.class);
        when(partGateway.findById(1L)).thenReturn(part);
        when(part.getActive()).thenReturn(true);
        when(part.getSellingPrice()).thenReturn(new BigDecimal("10.50")); // 10.50 * 2 = 21.00
        when(partInventoryGateway.getOnHand(1L)).thenReturn(5); // estoque suficiente

        BigDecimal total = service.calculateTotal(List.of(item));

        assertEquals(0, total.compareTo(new BigDecimal("21.00")));
        verify(partGateway).findById(1L);
        verify(partInventoryGateway).getOnHand(1L);
        verifyNoInteractions(serviceGateway);
    }

    @Test
    void calculateTotal_withMultipleParts_accumulatesAll_andChecksStockPerItem() {
        var item1 = new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 3.0); // 3 * 5.00 = 15.00
        var item2 = new BudgetItemRequestDTO(2L, ItemTypeEnum.PART, 2.0); // 2 * 7.25 = 14.50
        var item3 = new BudgetItemRequestDTO(3L, ItemTypeEnum.PART, 1.0); // 1 * 0.50 = 0.50

        PartDomain part1 = mock(PartDomain.class);
        PartDomain part2 = mock(PartDomain.class);
        PartDomain part3 = mock(PartDomain.class);

        when(partGateway.findById(1L)).thenReturn(part1);
        when(partGateway.findById(2L)).thenReturn(part2);
        when(partGateway.findById(3L)).thenReturn(part3);

        when(part1.getActive()).thenReturn(true);
        when(part2.getActive()).thenReturn(true);
        when(part3.getActive()).thenReturn(true);

        when(part1.getSellingPrice()).thenReturn(new BigDecimal("5.00"));
        when(part2.getSellingPrice()).thenReturn(new BigDecimal("7.25"));
        when(part3.getSellingPrice()).thenReturn(new BigDecimal("0.50"));

        when(partInventoryGateway.getOnHand(1L)).thenReturn(10);
        when(partInventoryGateway.getOnHand(2L)).thenReturn(10);
        when(partInventoryGateway.getOnHand(3L)).thenReturn(10);

        BigDecimal total = service.calculateTotal(List.of(item1, item2, item3));

        assertEquals(0, total.compareTo(new BigDecimal("30.00")));
        verify(partGateway).findById(1L);
        verify(partGateway).findById(2L);
        verify(partGateway).findById(3L);
        verify(partInventoryGateway).getOnHand(1L);
        verify(partInventoryGateway).getOnHand(2L);
        verify(partInventoryGateway).getOnHand(3L);
        verifyNoInteractions(serviceGateway);
    }

    @Test
    void calculateTotal_whenPartNotFound_throwsNotFound() {
        var item = new BudgetItemRequestDTO(99L, ItemTypeEnum.PART, 1.0);
        when(partGateway.findById(99L)).thenReturn(null);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("part.not.found.id"));
        verify(partGateway).findById(99L);
        verifyNoInteractions(serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenPartInactive_throwsIllegalState() {
        var item = new BudgetItemRequestDTO(5L, ItemTypeEnum.PART, 1.0);

        PartDomain part = mock(PartDomain.class);
        when(partGateway.findById(5L)).thenReturn(part);
        when(part.getActive()).thenReturn(false);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("part.inactive.id"));
        verify(partGateway).findById(5L);
        verifyNoInteractions(serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenPartQuantityIsFraction_throwsIllegalArgument() {
        var item = new BudgetItemRequestDTO(7L, ItemTypeEnum.PART, 1.5);
        PartDomain part = mock(PartDomain.class);
        when(partGateway.findById(7L)).thenReturn(part);
        when(part.getActive()).thenReturn(true);

        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("part.quantity.must.be.integer.id"));
        verify(partGateway).findById(7L);
        verifyNoInteractions(serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenInventoryRecordMissing_throwsNotFound() {
        var item = new BudgetItemRequestDTO(10L, ItemTypeEnum.PART, 2.0);

        PartDomain part = mock(PartDomain.class);
        when(partGateway.findById(10L)).thenReturn(part);
        when(part.getActive()).thenReturn(true);
        when(partInventoryGateway.getOnHand(10L)).thenReturn(null);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("part.inventory.not.found.id"));
        verify(partGateway).findById(10L);
        verify(partInventoryGateway).getOnHand(10L);
        verifyNoInteractions(serviceGateway);
    }

    @Test
    void calculateTotal_whenInsufficientStock_throwsIllegalState() {
        var item = new BudgetItemRequestDTO(11L, ItemTypeEnum.PART, 3.0);

        PartDomain part = mock(PartDomain.class);
        when(partGateway.findById(11L)).thenReturn(part);
        when(part.getActive()).thenReturn(true);
        when(partInventoryGateway.getOnHand(11L)).thenReturn(2);

        ConflictException ex = assertThrows(
                ConflictException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("insufficient.stock.partId"));
        verify(partGateway).findById(11L);
        verify(partInventoryGateway).getOnHand(11L);
        verifyNoInteractions(serviceGateway);
    }

    @Test
    void calculateTotal_withSingleService_usesBasePriceTimesQuantity() {
        var item = new BudgetItemRequestDTO(20L, ItemTypeEnum.SERVICE, 1.5);

        var serviceDomain = mock(dev.com.soat.autorepairshop.domain.entity.ServiceDomain.class);
        when(serviceGateway.findById(20L)).thenReturn(serviceDomain);
        when(serviceDomain.getBasePrice()).thenReturn(new BigDecimal("200.00"));

        BigDecimal total = service.calculateTotal(List.of(item));

        assertEquals(0, total.compareTo(new BigDecimal("300.00")));
        verify(serviceGateway).findById(20L);
        verifyNoInteractions(partGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenServiceNotFound_throwsNotFound() {
        var item = new BudgetItemRequestDTO(21L, ItemTypeEnum.SERVICE, 2.0);
        when(serviceGateway.findById(21L)).thenReturn(null);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("service.not.found.id"));
        verify(serviceGateway).findById(21L);
        verifyNoInteractions(partGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenTypeIsNull_throwsIllegalArgument() {
        var item = new BudgetItemRequestDTO(30L, null, 1.0);

        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.calculateTotal(List.of(item))
        );

        assertTrue(ex.getMessage().contains("item.type.required.id"));
        verifyNoInteractions(partGateway, serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenQuantityIsZero_throwsIllegalArgument() {
        var itemZero = new BudgetItemRequestDTO(31L, ItemTypeEnum.PART, 0.0);

        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.calculateTotal(List.of(itemZero))
        );

        assertTrue(ex.getMessage().contains("item.quantity.invalid.id"));
        verifyNoInteractions(partGateway, serviceGateway, partInventoryGateway);
    }

    @Test
    void calculateTotal_whenQuantityIsNegative_throwsIllegalArgument() {
        var itemNegative = new BudgetItemRequestDTO(32L, ItemTypeEnum.SERVICE, -1.0);

        DomainException ex = assertThrows(
                DomainException.class,
                () -> service.calculateTotal(List.of(itemNegative))
        );

        assertTrue(ex.getMessage().contains("item.quantity.invalid.id"));
        verifyNoInteractions(partGateway, serviceGateway, partInventoryGateway);
    }
}
