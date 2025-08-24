
package dev.com.soat.autorepairshop.infrastructure.adapter;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.BudgetItemAggregateRoot;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.mock.BudgetItemMock;
import dev.com.soat.autorepairshop.mock.BudgetMock;
import dev.com.soat.autorepairshop.mock.PartMock;
import dev.com.soat.autorepairshop.mock.ServiceMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes para TemplateEmailAdapter")
class TemplateEmailAdapterTest {

    private TemplateEmailAdapter templateEmailAdapter;
    private static final String SERVER_PORT = "8080";

    @BeforeEach
    void setUp() {
        templateEmailAdapter = new TemplateEmailAdapter();
        ReflectionTestUtils.setField(templateEmailAdapter, "serverPort", SERVER_PORT);
    }

    @Test
    @DisplayName("Deve gerar template de email com serviços e peças corretamente")
    void buildBudgetMessage_shouldGenerateCompleteTemplate() {
        // given
        String title = "Orçamento Auto Repair Shop";
        BudgetAggregateRoot budgetAggregateRoot = createMockBudgetAggregateRoot();

        // when
        String result = templateEmailAdapter.buildBudgetMessage(title, budgetAggregateRoot);

        // then
        assertNotNull(result);
    }

    private BudgetAggregateRoot createMockBudgetAggregateRoot() {
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