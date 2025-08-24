package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class BudgetMapperTest {

    @Test
    @DisplayName("Should map BudgetRequestDTO to BudgetInputDTO correctly")
    void mapFromBudgetRequestDTO_whenValid_shouldReturnBudgetInputDTO() {
        // given
        var items = List.of(
                new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 2.0),
                new BudgetItemRequestDTO(2L, ItemTypeEnum.SERVICE, 1.0)
        );

        var request = new BudgetRequestDTO(
                12345L,
                "notes",
                items
        );

        BudgetInputDTO result = BudgetMapper.map(request, 1L, 1L);

        assertThat(result).isNotNull();
        assertThat(result.serviceOrderId()).isEqualTo(12345L);
        assertThat(result.employeeId()).isEqualTo(1L);
        assertThat(result.notes()).isEqualTo("notes");
        assertThat(result.items()).containsExactlyElementsOf(items);
    }
}