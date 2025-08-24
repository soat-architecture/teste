package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.BudgetEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetEntityMapperTest {

    @Test
    @DisplayName("toDomain: deve mapear todos os campos corretamente")
    void toDomain_mapsAllFields() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LocalDateTime evaluatedAt = LocalDateTime.now().minusDays(1);
        BudgetEntity entity = new BudgetEntity(
                10L,                 // budgetId
                20L,                 // serviceOrderId
                3,                   // version
                new BigDecimal("123.45"),
                BudgetStatus.APPROVED, // status como String
                "some-notes",
                createdAt,
                evaluatedAt
        );

        // when
        BudgetDomain domain = BudgetEntityMapper.toDomain(entity);

        // then
        assertThat(domain).isNotNull();
        assertThat(domain.getIdentifier()).isEqualTo(10L);
        assertThat(domain.getServiceOrderId()).isEqualTo(20L);
        assertThat(domain.getVersion()).isEqualTo(3);
        assertThat(domain.getTotalAmount()).isEqualByComparingTo("123.45");
        assertThat(domain.getStatus()).isEqualTo(BudgetStatus.APPROVED);
        assertThat(domain.getNotes()).isEqualTo("some-notes");
        assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("toEntity: deve mapear todos os campos corretamente")
    void toEntity_mapsAllFields() {
        // given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        LocalDateTime evaluatedAt = LocalDateTime.now().minusDays(2);

        BudgetDomain domain = BudgetDomain.restore(
                5L,
                7L,
                2,
                new BigDecimal("999.99"),
                BudgetStatus.REJECTED,
                "why-not",
                createdAt,
                evaluatedAt
        );

        // when
        BudgetEntity entity = BudgetEntityMapper.toEntity(domain);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getBudgetId()).isEqualTo(5L);
        assertThat(entity.getServiceOrderId()).isEqualTo(7L);
        assertThat(entity.getVersion()).isEqualTo(2);
        assertThat(entity.getTotalAmount()).isEqualByComparingTo("999.99");
        assertThat(entity.getStatus()).isEqualTo(BudgetStatus.REJECTED); // string
        assertThat(entity.getNotes()).isEqualTo("why-not");
        assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        assertThat(entity.getEvaluatedAt()).isEqualTo(evaluatedAt);
    }

    @Test
    @DisplayName("round-trip: entity -> domain -> entity preserva valores essenciais")
    void roundTrip_preservesValues() {
        // given
        BudgetEntity original = new BudgetEntity(
                11L,
                22L,
                4,
                new BigDecimal("321.00"),
                BudgetStatus.PENDING_APPROVAL,
                "round-trip",
                LocalDateTime.now().minusDays(3),
                null
        );

        // when
        BudgetDomain domain = BudgetEntityMapper.toDomain(original);
        BudgetEntity back = BudgetEntityMapper.toEntity(domain);

        // then
        assertThat(back.getBudgetId()).isEqualTo(original.getBudgetId());
        assertThat(back.getServiceOrderId()).isEqualTo(original.getServiceOrderId());
        assertThat(back.getVersion()).isEqualTo(original.getVersion());
        assertThat(back.getTotalAmount()).isEqualByComparingTo(original.getTotalAmount());
        assertThat(back.getStatus()).isEqualTo(original.getStatus());
        assertThat(back.getNotes()).isEqualTo(original.getNotes());
        assertThat(back.getCreatedAt()).isEqualTo(original.getCreatedAt());
        assertThat(back.getEvaluatedAt()).isEqualTo(original.getEvaluatedAt());
    }
}

