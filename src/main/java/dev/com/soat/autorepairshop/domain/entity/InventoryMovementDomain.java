package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InventoryMovementDomain extends DomainEntity<Long> {
    private Long identifier;
    private Long partId;
    private Long userId;
    private Long serviceOrderId;
    private MovementType movementType;
    private Integer quantityChanged;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private String reason;
    private LocalDateTime createdAt;

    @Override
    public Long getIdentifier() {
        return this.identifier;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    protected LocalDateTime getUpdatedAt() {
        return null;
    }
}
