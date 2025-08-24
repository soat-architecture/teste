package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.InventoryMovementInputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryMovementEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InventoryMovementMapperTest {

    @Test
    void shouldMapFromEntityToDomain() {
        LocalDateTime now = LocalDateTime.now();

        InventoryMovementEntity entity = new InventoryMovementEntity(
                1L,
                10L,
                20L,
                30L,
                MovementType.OUTBOUND_SALE,
                5,
                100,
                95,
                "Retirada para manutenção",
                now
        );

        InventoryMovementDomain domain = InventoryMovementMapper.map(entity);

        assertNotNull(domain);
        assertEquals(1L, domain.getIdentifier());
        assertEquals(10L, domain.getPartId());
        assertEquals(20L, domain.getUserId());
        assertEquals(30L, domain.getServiceOrderId());
        assertEquals(MovementType.OUTBOUND_SALE, domain.getMovementType());
        assertEquals(5, domain.getQuantityChanged());
        assertEquals(100, domain.getQuantityBefore());
        assertEquals(95, domain.getQuantityAfter());
        assertEquals("Retirada para manutenção", domain.getReason());
        assertEquals(now, domain.getCreatedAt());
    }

    @Test
    void shouldMapFromInputDTOToDomain() {
        InventoryMovementInputDTO input = new InventoryMovementInputDTO(
                10L,
                20L,
                30L,
                MovementType.INBOUND,
                50,
                100,
                150,
                "Reposição de estoque"
        );

        InventoryMovementDomain domain = InventoryMovementMapper.map(input);

        assertNotNull(domain);
        assertNull(domain.getIdentifier());
        assertEquals(10L, domain.getPartId());
        assertEquals(20L, domain.getUserId());
        assertEquals(30L, domain.getServiceOrderId());
        assertEquals(MovementType.INBOUND, domain.getMovementType());
        assertEquals(50, domain.getQuantityChanged());
        assertEquals(100, domain.getQuantityBefore());
        assertEquals(150, domain.getQuantityAfter());
        assertEquals("Reposição de estoque", domain.getReason());
        assertNull(domain.getCreatedAt());
    }
}
