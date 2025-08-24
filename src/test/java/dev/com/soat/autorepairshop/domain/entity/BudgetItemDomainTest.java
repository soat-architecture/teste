package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetItemDomainTest {

    @Test
    @DisplayName("create(): deve criar com id null e createdAt ~ now")
    void create_setsFieldsProperly() {
        Long budgetId = 10L;
        ItemTypeEnum type = ItemTypeEnum.PART;
        Long itemId = 33L;
        Integer quantity = 2;
        BigDecimal unitPrice = new BigDecimal("123.45");

        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        BudgetItemDomain d = BudgetItemDomain.create(budgetId, type, itemId, quantity, unitPrice);

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        assertThat(d.getIdentifier()).isNull();
        assertThat(d.getBudgetId()).isEqualTo(budgetId);
        assertThat(d.getItemType()).isEqualTo(type);
        assertThat(d.getPartId()).isEqualTo(itemId);
        assertThat(d.getQuantity()).isEqualTo(quantity);
        assertThat(d.getUnitPrice()).isEqualByComparingTo("123.45");
        assertThat(d.getCreatedAt()).isBetween(before, after);

        assertThat(d.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("restore(): deve popular todos os campos; updatedAt permanece null")
    void restore_setsAllFields() {
        // given
        Long id = 77L;
        Long budgetId = 20L;
        ItemTypeEnum type = ItemTypeEnum.SERVICE;
        Long serviceId = 99L;
        Integer quantity = 5;
        BigDecimal unitPrice = new BigDecimal("9.99");
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);

        // when
        BudgetItemDomain d = BudgetItemDomain.restore(
                id, budgetId, type, serviceId, null, quantity, unitPrice, createdAt
        );

        // then
        assertThat(d.getIdentifier()).isEqualTo(id);
        assertThat(d.getBudgetId()).isEqualTo(budgetId);
        assertThat(d.getItemType()).isEqualTo(type);
        assertThat(d.getServiceId()).isEqualTo(serviceId);
        assertThat(d.getQuantity()).isEqualTo(quantity);
        assertThat(d.getUnitPrice()).isEqualByComparingTo("9.99");
        assertThat(d.getCreatedAt()).isEqualTo(createdAt);
        assertThat(d.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("getUpdatedAt(): retorna null (não há completedAt no modelo)")
    void getUpdatedAt_null() {
        BudgetItemDomain d = BudgetItemDomain.restore(
                1L, 2L, ItemTypeEnum.PART, 3L, 1L, 1, new BigDecimal("1.00"),
                LocalDateTime.now().minusDays(2)
        );
        assertThat(d.getUpdatedAt()).isNull();
    }
}
