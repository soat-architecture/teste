package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Testes para BudgetDomain")
class BudgetDomainTest {

    private static final Long SERVICE_ORDER_ID = 1L;
    private static final Long IDENTIFIER = 1L;
    private static final Integer VERSION = 1;
    private static final BigDecimal TOTAL_AMOUNT = BigDecimal.valueOf(100.0);
    private static final String NOTES = "Observações de teste";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime EVALUATED_AT = LocalDateTime.now().plusHours(1);

    @Test
    @DisplayName("Deve criar um novo orçamento com sucesso")
    void shouldCreateNewBudget() {
        // when
        BudgetDomain budget = BudgetDomain.create(SERVICE_ORDER_ID, TOTAL_AMOUNT, NOTES);

        // then
        assertNotNull(budget);
        assertEquals(SERVICE_ORDER_ID, budget.getServiceOrderId());
        assertEquals(TOTAL_AMOUNT, budget.getTotalAmount());
        assertEquals(NOTES, budget.getNotes());
        assertEquals(BudgetStatus.PENDING_APPROVAL, budget.getStatus());
        assertNull(budget.getIdentifier());
        assertNull(budget.getVersion());
        assertNotNull(budget.getCreatedAt());
        assertNull(budget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve restaurar um orçamento existente com sucesso")
    void shouldRestoreBudget() {
        // when
        BudgetDomain budget = BudgetDomain.restore(
                IDENTIFIER,
                SERVICE_ORDER_ID,
                VERSION,
                TOTAL_AMOUNT,
                BudgetStatus.APPROVED,
                NOTES,
                CREATED_AT,
                EVALUATED_AT
        );

        // then
        assertNotNull(budget);
        assertEquals(IDENTIFIER, budget.getIdentifier());
        assertEquals(SERVICE_ORDER_ID, budget.getServiceOrderId());
        assertEquals(VERSION, budget.getVersion());
        assertEquals(TOTAL_AMOUNT, budget.getTotalAmount());
        assertEquals(BudgetStatus.APPROVED, budget.getStatus());
        assertEquals(NOTES, budget.getNotes());
        assertEquals(CREATED_AT, budget.getCreatedAt());
        assertEquals(EVALUATED_AT, budget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve criar um orçamento com versão específica")
    void shouldCreateBudgetWithVersion() {
        // when
        BudgetDomain budget = BudgetDomain.createWithVersion(
                SERVICE_ORDER_ID,
                TOTAL_AMOUNT,
                NOTES,
                VERSION,
                CREATED_AT
        );

        // then
        assertNotNull(budget);
        assertEquals(SERVICE_ORDER_ID, budget.getServiceOrderId());
        assertEquals(VERSION, budget.getVersion());
        assertEquals(TOTAL_AMOUNT, budget.getTotalAmount());
        assertEquals(BudgetStatus.PENDING_APPROVAL, budget.getStatus());
        assertEquals(NOTES, budget.getNotes());
        assertEquals(CREATED_AT, budget.getCreatedAt());
        assertNull(budget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve criar uma nova versão a partir de um orçamento existente")
    void shouldCreateNewVersionFromExistingBudget() {
        // given
        BudgetDomain baseBudget = BudgetDomain.create(SERVICE_ORDER_ID, TOTAL_AMOUNT, NOTES);
        BigDecimal newAmount = BigDecimal.valueOf(150.0);
        String newNotes = "Novas observações";
        int newVersion = 2;

        // when
        BudgetDomain newBudget = BudgetDomain.newVersionFrom(baseBudget, newAmount, newNotes, newVersion);

        // then
        assertNotNull(newBudget);
        assertEquals(baseBudget.getServiceOrderId(), newBudget.getServiceOrderId());
        assertEquals(newVersion, newBudget.getVersion());
        assertEquals(newAmount, newBudget.getTotalAmount());
        assertEquals(BudgetStatus.PENDING_APPROVAL, newBudget.getStatus());
        assertEquals(newNotes, newBudget.getNotes());
        assertEquals(baseBudget.getCreatedAt(), newBudget.getCreatedAt());
        assertNull(newBudget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve rejeitar um orçamento com sucesso")
    void shouldRejectBudget() {
        // given
        BudgetDomain budget = BudgetDomain.create(SERVICE_ORDER_ID, TOTAL_AMOUNT, NOTES);

        // when
        budget.notApproved();

        // then
        assertEquals(BudgetStatus.REJECTED, budget.getStatus());
        assertNotNull(budget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve aprovar um orçamento com sucesso")
    void shouldApproveBudget() {
        // given
        BudgetDomain budget = BudgetDomain.create(SERVICE_ORDER_ID, TOTAL_AMOUNT, NOTES);

        // when
        budget.approved();

        // then
        assertEquals(BudgetStatus.APPROVED, budget.getStatus());
        assertNotNull(budget.getEvaluatedAt());
    }

    @Test
    @DisplayName("Deve criar orçamento com status padrão quando status é nulo")
    void shouldCreateBudgetWithDefaultStatusWhenNull() {
        // when
        BudgetDomain budget = new BudgetDomain(
                IDENTIFIER,
                SERVICE_ORDER_ID,
                VERSION,
                TOTAL_AMOUNT,
                null,
                NOTES,
                CREATED_AT,
                EVALUATED_AT
        );

        // then
        assertEquals(BudgetStatus.PENDING_APPROVAL, budget.getStatus());
    }

    @ParameterizedTest
    @EnumSource(BudgetStatus.class)
    @DisplayName("Deve criar orçamento com todos os status possíveis")
    void shouldCreateBudgetWithAllPossibleStatus(BudgetStatus status) {
        // when
        BudgetDomain budget = new BudgetDomain(
                IDENTIFIER,
                SERVICE_ORDER_ID,
                VERSION,
                TOTAL_AMOUNT,
                status,
                NOTES,
                CREATED_AT,
                EVALUATED_AT
        );

        // then
        assertEquals(status, budget.getStatus());
    }

    @Test
    @DisplayName("Deve retornar evaluatedAt como updatedAt")
    void shouldReturnEvaluatedAtAsUpdatedAt() {
        // given
        BudgetDomain budget = BudgetDomain.restore(
                IDENTIFIER,
                SERVICE_ORDER_ID,
                VERSION,
                TOTAL_AMOUNT,
                BudgetStatus.APPROVED,
                NOTES,
                CREATED_AT,
                EVALUATED_AT
        );

        // when
        LocalDateTime updatedAt = budget.getUpdatedAt();

        // then
        assertEquals(EVALUATED_AT, updatedAt);
    }
}