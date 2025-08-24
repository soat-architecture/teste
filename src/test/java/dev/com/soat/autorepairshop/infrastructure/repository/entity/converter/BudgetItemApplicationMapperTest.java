package dev.com.soat.autorepairshop.infrastructure.repository.entity.converter;

import dev.com.soat.autorepairshop.application.mapper.BudgetItemApplicationMapper;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetItemApplicationMapperTest {

    private BudgetItemApplicationMapperTest(){}

    @Test
    @DisplayName("toDomain: deve mapear corretamente id, tipo, quantidade, preço e budgetId")
    void toDomain_mapsAllFields() {
        // given
        Long budgetId = 10L;
        BudgetItemRequestDTO dto = new BudgetItemRequestDTO(
                55L,                 // item id
                ItemTypeEnum.PART,   // type
                2.0                  // quantity (Double)
        );
        BigDecimal unitPrice = new BigDecimal("123.45");

        // when
        BudgetItemDomain domain = BudgetItemApplicationMapper.toDomain(dto, budgetId, unitPrice);

        // then
        assertThat(domain).isNotNull();
        assertThat(domain.getIdentifier()).isNull();            // create() -> id null
        assertThat(domain.getBudgetId()).isEqualTo(budgetId);
        assertThat(domain.getItemType()).isEqualTo(ItemTypeEnum.PART);
        assertThat(domain.getPartId()).isEqualTo(55L);
        assertThat(domain.getQuantity()).isEqualTo(2);          // Double -> intValue()
        assertThat(domain.getUnitPrice()).isEqualByComparingTo("123.45");
        assertThat(domain.getCreatedAt()).isNotNull();          // create() seta agora
    }

    @Test
    @DisplayName("toDomain: deve converter quantidade Double para inteiro via intValue()")
    void toDomain_convertsQuantityDoubleToInt() {
        // given
        Long budgetId = 99L;
        BudgetItemRequestDTO dto = new BudgetItemRequestDTO(
                7L,
                ItemTypeEnum.SERVICE,
                3.9  // será truncado para 3 por intValue()
        );

        // when
        BudgetItemDomain domain = BudgetItemApplicationMapper.toDomain(dto, budgetId, new BigDecimal("1.00"));

        // then
        assertThat(domain.getQuantity()).isEqualTo(3);
        assertThat(domain.getItemType()).isEqualTo(ItemTypeEnum.SERVICE);
        assertThat(domain.getServiceId()).isEqualTo(7L);
        assertThat(domain.getBudgetId()).isEqualTo(99L);
    }
}
