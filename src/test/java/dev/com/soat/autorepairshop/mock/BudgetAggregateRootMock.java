package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;

import java.util.List;

public class BudgetAggregateRootMock {

    public static BudgetAggregateRoot buildDomain() {
        final var client = new ClientDomain(1L, "Teste", "71019345012", "(11) 97653-2346",
                "teste@teste.com", ClientStatus.ACTIVE, null, null);

        final var budget = BudgetMock.buildDomain();
        final var service = ServiceMock.buildDomain();
        final var part = PartMock.buildDomain();
        final var item = BudgetItemMock.buildDomain();
        final var itemRoot = BudgetItemAggregateRoot.create(
                service,
                part,
                item
        );



        return BudgetAggregateRoot.create(
                client,
                budget,
                List.of(itemRoot)
        );
    }
}
