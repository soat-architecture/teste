package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.application.models.output.BudgetOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetMock {
    public static BudgetDomain buildDomain(){
        return new BudgetDomain(
                1L,
                1L,
                1,
                BigDecimal.valueOf(100.0),
                BudgetStatus.PENDING_APPROVAL,
                "Notas de teste",
                LocalDateTime.now(),
                null
        );
    }

    public static BudgetOutputDTO buildOutputDTO(){
        return new BudgetOutputDTO(
                1L,
                1L,
                1,
                BigDecimal.valueOf(100.0),
                BudgetStatus.PENDING_APPROVAL,
                "Notas de teste",
                LocalDateTime.now(),
                null
        );
    }
}
