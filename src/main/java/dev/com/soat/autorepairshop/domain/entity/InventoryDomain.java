package dev.com.soat.autorepairshop.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
public class InventoryDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final Integer quantityOnHand;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Override
    public Long getIdentifier() {
        return identifier;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public InventoryDomain(Long identifier, Integer quantityOnHand, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.identifier = identifier;
        this.quantityOnHand = quantityOnHand;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}