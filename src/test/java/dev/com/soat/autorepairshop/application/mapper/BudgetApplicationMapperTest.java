
package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.BudgetDomain;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Testes para BudgetApplicationMapper")
class BudgetApplicationMapperTest {

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long BUDGET_ID = 1L;
    private static final Long EMPLOYEE_ID = 1L;
    private static final String NOTES = "Notas de teste";
    private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(100.0);
    private static final int VERSION = 1;
    private static final LocalDateTime NOW = LocalDateTime.now();

    @Test
    @DisplayName("Deve mapear BudgetInputDTO para BudgetDomain corretamente")
    void shouldMapBudgetInputDTOToBudgetDomain() {
        // given
        BudgetInputDTO inputDTO = createBudgetInputDTO();

        // when
        BudgetDomain result = BudgetApplicationMapper.toOutputDTO(inputDTO, TOTAL_AMOUNT);

        // then
        assertNotNull(result);
        assertEquals(SERVICE_ORDER_ID, result.getServiceOrderId());
        assertEquals(TOTAL_AMOUNT, result.getTotalAmount());
        assertEquals(NOTES, result.getNotes());
        assertEquals(BudgetStatus.PENDING_APPROVAL, result.getStatus());
    }

    @Test
    @DisplayName("Deve mapear BudgetDomain para BudgetOutputDTO corretamente")
    void shouldMapBudgetDomainToBudgetOutputDTO() {
        // given
        BudgetDomain domain = createBudgetDomain();

        // when
        BudgetOutputDTO result = BudgetApplicationMapper.toOutputDTO(domain);

        // then
        assertNotNull(result);
        assertEquals(SERVICE_ORDER_ID, result.serviceOrderId());
        assertEquals(VERSION, result.version());
        assertEquals(TOTAL_AMOUNT, result.totalAmount());
        assertEquals(BudgetStatus.PENDING_APPROVAL, result.status());
        assertEquals(NOTES, result.notes());
        assertEquals(NOW, result.createdAt());
        assertEquals(NOW, result.evaluatedAt());
    }

    @Test
    @DisplayName("Deve criar novo BudgetDomain com versão específica")
    void shouldCreateBudgetDomainWithSpecificVersion() {
        // given
        BudgetInputDTO inputDTO = createBudgetInputDTO();

        // when
        BudgetDomain result = BudgetApplicationMapper.toDomainForCreate(
                inputDTO,
                TOTAL_AMOUNT,
                VERSION,
                NOW
        );

        // then
        assertNotNull(result);
        assertEquals(SERVICE_ORDER_ID, result.getServiceOrderId());
        assertEquals(TOTAL_AMOUNT, result.getTotalAmount());
        assertEquals(NOTES, result.getNotes());
        assertEquals(VERSION, result.getVersion());
        assertEquals(NOW, result.getCreatedAt());
        assertEquals(BudgetStatus.PENDING_APPROVAL, result.getStatus());
    }

    @Test
    @DisplayName("Deve criar nova versão de BudgetDomain a partir de base existente")
    void shouldCreateNewVersionFromExistingBudget() {
        // given
        BudgetDomain baseDomain = createBudgetDomain();
        BudgetInputDTO inputDTO = createBudgetInputDTO();
        int newVersion = VERSION + 1;

        // when
        BudgetDomain result = BudgetApplicationMapper.toDomainForNewVersionFrom(
                baseDomain,
                inputDTO,
                TOTAL_AMOUNT,
                newVersion
        );

        // then
        assertNotNull(result);
        assertEquals(SERVICE_ORDER_ID, result.getServiceOrderId());
        assertEquals(TOTAL_AMOUNT, result.getTotalAmount());
        assertEquals(NOTES, result.getNotes());
        assertEquals(newVersion, result.getVersion());
        assertEquals(NOW, result.getCreatedAt());
        assertEquals(BudgetStatus.PENDING_APPROVAL, result.getStatus());
    }

    private BudgetInputDTO createBudgetInputDTO() {
        return new BudgetInputDTO(
                BUDGET_ID,
                SERVICE_ORDER_ID,
                EMPLOYEE_ID,
                NOTES,
                List.of()
        );
    }

    private BudgetDomain createBudgetDomain() {
        return new BudgetDomain(
                BUDGET_ID,
                SERVICE_ORDER_ID,
                VERSION,
                TOTAL_AMOUNT,
                BudgetStatus.PENDING_APPROVAL,
                NOTES,
                NOW,
                NOW
        );
    }
}