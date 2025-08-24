package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes para InventoryMovementDomain")
class InventoryMovementDomainTest {

    private static final Long IDENTIFIER = 1L;
    private static final Long PART_ID = 2L;
    private static final Long USER_ID = 3L;
    private static final Long SERVICE_ORDER_ID = 4L;
    private static final Integer QUANTITY_CHANGED = 5;
    private static final Integer QUANTITY_BEFORE = 10;
    private static final Integer QUANTITY_AFTER = 15;
    private static final String REASON = "Teste de movimentação";
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();

    @Test
    @DisplayName("Deve criar movimento de inventário usando builder com sucesso")
    void shouldCreateInventoryMovementUsingBuilder() {
        // when
        InventoryMovementDomain movement = InventoryMovementDomain.builder()
                .identifier(IDENTIFIER)
                .partId(PART_ID)
                .userId(USER_ID)
                .serviceOrderId(SERVICE_ORDER_ID)
                .movementType(MovementType.INBOUND)
                .quantityChanged(QUANTITY_CHANGED)
                .quantityBefore(QUANTITY_BEFORE)
                .quantityAfter(QUANTITY_AFTER)
                .reason(REASON)
                .createdAt(CREATED_AT)
                .build();

        // then
        assertNotNull(movement);
        assertEquals(IDENTIFIER, movement.getIdentifier());
        assertEquals(PART_ID, movement.getPartId());
        assertEquals(USER_ID, movement.getUserId());
        assertEquals(SERVICE_ORDER_ID, movement.getServiceOrderId());
        assertEquals(MovementType.INBOUND, movement.getMovementType());
        assertEquals(QUANTITY_CHANGED, movement.getQuantityChanged());
        assertEquals(QUANTITY_BEFORE, movement.getQuantityBefore());
        assertEquals(QUANTITY_AFTER, movement.getQuantityAfter());
        assertEquals(REASON, movement.getReason());
        assertEquals(CREATED_AT, movement.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar movimento de inventário usando construtor com sucesso")
    void shouldCreateInventoryMovementUsingConstructor() {
        // when
        InventoryMovementDomain movement = new InventoryMovementDomain(
                IDENTIFIER,
                PART_ID,
                USER_ID,
                SERVICE_ORDER_ID,
                MovementType.OUTBOUND_SALE,
                QUANTITY_CHANGED,
                QUANTITY_BEFORE,
                QUANTITY_AFTER,
                REASON,
                CREATED_AT
        );

        // then
        assertNotNull(movement);
        assertEquals(IDENTIFIER, movement.getIdentifier());
        assertEquals(PART_ID, movement.getPartId());
        assertEquals(USER_ID, movement.getUserId());
        assertEquals(SERVICE_ORDER_ID, movement.getServiceOrderId());
        assertEquals(MovementType.OUTBOUND_SALE, movement.getMovementType());
        assertEquals(QUANTITY_CHANGED, movement.getQuantityChanged());
        assertEquals(QUANTITY_BEFORE, movement.getQuantityBefore());
        assertEquals(QUANTITY_AFTER, movement.getQuantityAfter());
        assertEquals(REASON, movement.getReason());
        assertEquals(CREATED_AT, movement.getCreatedAt());
    }

    @Test
    @DisplayName("Deve criar movimento de inventário usando construtor vazio")
    void shouldCreateInventoryMovementUsingEmptyConstructor() {
        // when
        InventoryMovementDomain movement = new InventoryMovementDomain();

        // then
        assertNotNull(movement);
        assertNull(movement.getIdentifier());
        assertNull(movement.getPartId());
        assertNull(movement.getUserId());
        assertNull(movement.getServiceOrderId());
        assertNull(movement.getMovementType());
        assertNull(movement.getQuantityChanged());
        assertNull(movement.getQuantityBefore());
        assertNull(movement.getQuantityAfter());
        assertNull(movement.getReason());
        assertNull(movement.getCreatedAt());
    }

    @ParameterizedTest
    @EnumSource(MovementType.class)
    @DisplayName("Deve criar movimento de inventário com todos os tipos de movimento possíveis")
    void shouldCreateInventoryMovementWithAllMovementTypes(MovementType movementType) {
        // when
        InventoryMovementDomain movement = InventoryMovementDomain.builder()
                .movementType(movementType)
                .build();

        // then
        assertEquals(movementType, movement.getMovementType());
    }

    @Test
    @DisplayName("Deve retornar null para updatedAt")
    void shouldReturnNullForUpdatedAt() {
        // given
        InventoryMovementDomain movement = new InventoryMovementDomain();

        // when
        LocalDateTime updatedAt = movement.getUpdatedAt();

        // then
        assertNull(updatedAt);
    }

    @Test
    @DisplayName("Deve retornar createdAt corretamente")
    void shouldReturnCreatedAt() {
        // given
        InventoryMovementDomain movement = InventoryMovementDomain.builder()
                .createdAt(CREATED_AT)
                .build();

        // when
        LocalDateTime returnedCreatedAt = movement.getCreatedAt();

        // then
        assertEquals(CREATED_AT, returnedCreatedAt);
    }

    @Test
    @DisplayName("Deve retornar identifier corretamente")
    void shouldReturnIdentifier() {
        // given
        InventoryMovementDomain movement = InventoryMovementDomain.builder()
                .identifier(IDENTIFIER)
                .build();

        // when
        Long returnedIdentifier = movement.getIdentifier();

        // then
        assertEquals(IDENTIFIER, returnedIdentifier);
    }
}