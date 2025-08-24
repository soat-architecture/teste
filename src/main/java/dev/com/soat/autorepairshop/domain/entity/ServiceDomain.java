package dev.com.soat.autorepairshop.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ServiceDomain extends DomainEntity<Long>{

    private final Long identifier;
    private final String name;
    private final String description;
    private final BigDecimal basePrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Override
    public Long getIdentifier() {
        return this.identifier;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public ServiceDomain(Long identifier, String name, String description, BigDecimal basePrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceDomain create(String name, String description, BigDecimal basePrice) {
        return new ServiceDomain(null, name, description, basePrice, null, null);
    }

    public static ServiceDomain restore(Long identifier, String name, String description, BigDecimal basePrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new ServiceDomain(identifier, name, description, basePrice, createdAt, updatedAt);
    }
}
