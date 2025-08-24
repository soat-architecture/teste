package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class InventoryMovementApplicationMapperTest {

    @Test
    void map_shouldCopyAllFieldsFromDomainToDto() {
        // given
        Long id = 100L;
        Long partId = 10L;
        Long userId = 20L;
        Long soId = 30L;
        MovementType type = MovementType.OUTBOUND_SALE;
        int qtyChanged = -3;
        int qtyBefore = 8;
        int qtyAfter = 5;
        String reason = "BUDGET_UPDATE 123";
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(3);

        InventoryMovementDomain domain = new InventoryMovementDomain(
                id, partId, userId, soId, type, qtyChanged, qtyBefore, qtyAfter, reason, createdAt
        );

        // when
        InventoryMovementOutputDTO dto = InventoryMovementApplicationMapper.map(domain);

        // then
        assertThat(dto.identifier()).isEqualTo(id);
        assertThat(dto.partId()).isEqualTo(partId);
        assertThat(dto.userId()).isEqualTo(userId);
        assertThat(dto.serviceOrderId()).isEqualTo(soId);
        assertThat(dto.movementType()).isEqualTo(type);
        assertThat(dto.quantityChanged()).isEqualTo(qtyChanged);
        assertThat(dto.quantityBefore()).isEqualTo(qtyBefore);
        assertThat(dto.quantityAfter()).isEqualTo(qtyAfter);
        assertThat(dto.reason()).isEqualTo(reason);
        assertThat(dto.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void newMovement_shouldCreateDomainWithNullIdAndNullCreatedAt_andProvidedFields() {
        // given
        Long partId = 11L;
        Long userId = 22L;
        Long soId = 33L;
        MovementType type = MovementType.ADJUSTMENT;
        int qtyChanged = 7;
        int qtyBefore = 10;
        int qtyAfter = 17;
        String reason = "Manual adjust";

        // when
        InventoryMovementDomain d = InventoryMovementApplicationMapper.newMovement(
                partId, userId, soId, type, qtyChanged, qtyBefore, qtyAfter, reason
        );

        // then
        assertThat(d.getIdentifier()).isNull();
        assertThat(d.getCreatedAt()).isNull();
        assertThat(d.getPartId()).isEqualTo(partId);
        assertThat(d.getUserId()).isEqualTo(userId);
        assertThat(d.getServiceOrderId()).isEqualTo(soId);
        assertThat(d.getMovementType()).isEqualTo(type);
        assertThat(d.getQuantityChanged()).isEqualTo(qtyChanged);
        assertThat(d.getQuantityBefore()).isEqualTo(qtyBefore);
        assertThat(d.getQuantityAfter()).isEqualTo(qtyAfter);
        assertThat(d.getReason()).isEqualTo(reason);
    }

    @Test
    void outboundSale_shouldProduceNegativeChanged_andAfterEqualsBeforeMinusConsumed() {
        // given
        Long partId = 5L;
        Long userId = 6L;
        Long soId = 7L;
        int consumed = 4;
        int before = 15;
        String reason = "budget.update.consume.new.items";

        // when
        InventoryMovementDomain d = InventoryMovementApplicationMapper.outboundSale(
                partId, userId, soId, consumed, before, reason
        );

        // then
        assertThat(d.getIdentifier()).isNull();
        assertThat(d.getCreatedAt()).isNull();
        assertThat(d.getMovementType()).isEqualTo(MovementType.OUTBOUND_SALE);
        assertThat(d.getQuantityChanged()).isEqualTo(-consumed);
        assertThat(d.getQuantityBefore()).isEqualTo(before);
        assertThat(d.getQuantityAfter()).isEqualTo(before - consumed);
        assertThat(d.getPartId()).isEqualTo(partId);
        assertThat(d.getUserId()).isEqualTo(userId);
        assertThat(d.getServiceOrderId()).isEqualTo(soId);
        assertThat(d.getReason()).isEqualTo(reason);
    }

    @Test
    void adjustmentIncrease_shouldProducePositiveChanged_andAfterEqualsBeforePlusQty() {
        // given
        Long partId = 1L;
        Long userId = 2L;
        Long soId = 3L;
        int qty = 9;
        int before = 1;
        String reason = "inventory.correction";

        // when
        InventoryMovementDomain d = InventoryMovementApplicationMapper.adjustmentIncrease(
                partId, userId, soId, qty, before, reason
        );

        // then
        assertThat(d.getIdentifier()).isNull();
        assertThat(d.getCreatedAt()).isNull();
        assertThat(d.getMovementType()).isEqualTo(MovementType.ADJUSTMENT);
        assertThat(d.getQuantityChanged()).isEqualTo(qty);
        assertThat(d.getQuantityBefore()).isEqualTo(before);
        assertThat(d.getQuantityAfter()).isEqualTo(before + qty);
        assertThat(d.getPartId()).isEqualTo(partId);
        assertThat(d.getUserId()).isEqualTo(userId);
        assertThat(d.getServiceOrderId()).isEqualTo(soId);
        assertThat(d.getReason()).isEqualTo(reason);
    }
}

