package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.domain.entity.BudgetItemDomain;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BudgetItemMock {

    public static List<BudgetItemDomain> buildListDomain(){
        return List.of(buildDomain());
    }

    public static BudgetItemDomain buildDomain() {
        return new BudgetItemDomain(
                1L,
                1L,
                ItemTypeEnum.PART,
                null,
                1L,
                1,
                BigDecimal.valueOf(100.0),
                LocalDateTime.now()
        );
    }
}
