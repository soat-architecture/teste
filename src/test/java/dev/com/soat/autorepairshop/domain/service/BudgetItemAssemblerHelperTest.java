package dev.com.soat.autorepairshop.domain.service;

import dev.com.soat.autorepairshop.application.mapper.BudgetItemApplicationMapper;
import dev.com.soat.autorepairshop.application.helper.BudgetItemAssemblerHelper;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.gateway.PartGateway;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BudgetItemAssemblerHelperTest {

    private PartGateway partGateway;
    private ServiceGateway serviceGateway;
    private BudgetItemAssemblerHelper service;

    @BeforeEach
    void setUp() {
        partGateway = mock(PartGateway.class);
        service = new BudgetItemAssemblerHelper(partGateway, serviceGateway);
    }

    @Test
    void assemble_whenItemsEmpty_returnsEmptyList() {
        List<BudgetItemDomain> result = service.assemble(List.of(), 123L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(partGateway);
    }

    @Test
    void assemble_mapsEachItemUsingUnitPriceFromPart_andReturnsListInOrder() {
        // given
        Long budgetId = 100L;
        var item1 = new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 3.0);
        var item2 = new BudgetItemRequestDTO(2L, ItemTypeEnum.PART, 1.0);

        PartDomain part1 = mock(PartDomain.class);
        PartDomain part2 = mock(PartDomain.class);

        when(partGateway.findById(1L)).thenReturn(part1);
        when(partGateway.findById(2L)).thenReturn(part2);

        when(part1.getSellingPrice()).thenReturn(new BigDecimal("10.00"));
        when(part2.getSellingPrice()).thenReturn(new BigDecimal("7.50"));

        BudgetItemDomain mapped1 = mock(BudgetItemDomain.class);
        BudgetItemDomain mapped2 = mock(BudgetItemDomain.class);

        try (MockedStatic<BudgetItemApplicationMapper> mocked =
                     mockStatic(BudgetItemApplicationMapper.class)) {

            mocked.when(() -> BudgetItemApplicationMapper.toDomain(eq(item1), eq(budgetId), eq(new BigDecimal("10.00"))))
                    .thenReturn(mapped1);
            mocked.when(() -> BudgetItemApplicationMapper.toDomain(eq(item2), eq(budgetId), eq(new BigDecimal("7.50"))))
                    .thenReturn(mapped2);

            // when
            List<BudgetItemDomain> result = service.assemble(List.of(item1, item2), budgetId);

            // then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertSame(mapped1, result.get(0));
            assertSame(mapped2, result.get(1));

            verify(partGateway).findById(1L);
            verify(partGateway).findById(2L);
            verifyNoMoreInteractions(partGateway);

            mocked.verify(() -> BudgetItemApplicationMapper.toDomain(eq(item1), eq(budgetId), eq(new BigDecimal("10.00"))));
            mocked.verify(() -> BudgetItemApplicationMapper.toDomain(eq(item2), eq(budgetId), eq(new BigDecimal("7.50"))));
        }
    }
}