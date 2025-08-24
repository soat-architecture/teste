package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetItemEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetItemEntityMapperTest {

    @Test
    @DisplayName("toDomain: deve mapear todos os campos corretamente")
    void toDomain_mapsAllFields() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        BudgetItemEntity entity = new BudgetItemEntity(
                11L,              // id
                22L,              // budgetId
                ItemTypeEnum.PART, // itemType STRING
                33L,                // part id
                33L,                // service id
                4,                  // quantity
                new BigDecimal("12.34"), // unitPrice
                createdAt
        );

        // when
        BudgetItemDomain domain = BudgetItemEntityMapper.toDomain(entity);

        // then
        assertThat(domain).isNotNull();
        assertThat(domain.getIdentifier()).isEqualTo(11L);
        assertThat(domain.getBudgetId()).isEqualTo(22L);
        assertThat(domain.getItemType()).isEqualTo(ItemTypeEnum.PART);
        assertThat(domain.getPartId()).isEqualTo(33L);
        assertThat(domain.getQuantity()).isEqualTo(4);
        assertThat(domain.getUnitPrice()).isEqualByComparingTo("12.34");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("toEntity: deve mapear todos os campos corretamente")
    void toEntity_mapsAllFields() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);

        BudgetItemDomain domain = BudgetItemDomain.restore(
                44L,                  // partId
                55L,                  // budgetId
                ItemTypeEnum.SERVICE, // itemType
                66L,                  // itemId
                null,
                7,                    // quantity
                new BigDecimal("99.99"),
                createdAt
        );

        // when
        BudgetItemEntity entity = BudgetItemEntityMapper.toEntity(domain);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(44L);
        assertThat(entity.getBudgetId()).isEqualTo(55L);
        assertThat(entity.getItemType()).isEqualTo(ItemTypeEnum.SERVICE); // STRING
        assertThat(entity.getServiceId()).isEqualTo(66L);
        assertThat(entity.getQuantity()).isEqualTo(7);
        assertThat(entity.getUnitPrice()).isEqualByComparingTo("99.99");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("round-trip: entity -> domain -> entity preserva campos essenciais")
    void roundTrip_preservesValues() {
        BudgetItemEntity original = new BudgetItemEntity(
                9L, 8L, ItemTypeEnum.PART, 7L, 6L, 9,
                new BigDecimal("5.55"), LocalDateTime.now().minusDays(1)
        );

        BudgetItemDomain domain = BudgetItemEntityMapper.toDomain(original);
        BudgetItemEntity back = BudgetItemEntityMapper.toEntity(domain);

        assertThat(back.getId()).isEqualTo(original.getId());
        assertThat(back.getBudgetId()).isEqualTo(original.getBudgetId());
        assertThat(back.getItemType()).isEqualTo(original.getItemType());
        assertThat(back.getPartId()).isEqualTo(original.getPartId());
        assertThat(back.getServiceId()).isEqualTo(original.getServiceId());
        assertThat(back.getQuantity()).isEqualTo(original.getQuantity());
        assertThat(back.getUnitPrice()).isEqualByComparingTo(original.getUnitPrice());
        assertThat(back.getCreatedAt()).isEqualTo(original.getCreatedAt());
    }
}

