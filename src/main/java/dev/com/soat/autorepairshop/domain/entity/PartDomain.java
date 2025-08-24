package dev.com.soat.autorepairshop.domain.entity;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PartDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final String name;
    private final String sku;
    private final String description;
    private final String brand;
    private final BigDecimal sellingPrice;
    private final BigDecimal buyPrice;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PartDomain(
            final Long identifier,
            final String name,
            final String sku,
            final String description,
            final String brand,
            final BigDecimal sellingPrice,
            final BigDecimal buyPrice,
            final Boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        this.identifier = identifier;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.brand = brand;
        this.sellingPrice = sellingPrice;
        this.buyPrice = buyPrice;
        this.active = active != null ? active : true;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static PartDomain create(
            final String name,
            final String sku,
            final String description,
            final String brand,
            final BigDecimal sellingPrice,
            final BigDecimal buyPrice
    )  {
        var createdAt = LocalDateTime.now();

        return new PartDomain(
                null,
                name,
                sku,
                description,
                brand,
                sellingPrice,
                buyPrice,
                Boolean.TRUE,
                createdAt,
                null
        );
    }

    public static PartDomain restore(
            final Long identifier,
            final String name,
            final String sku,
            final String description,
            final String brand,
            final BigDecimal sellingPrice,
            final BigDecimal buyPrice,
            final Boolean active,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
            ){

        return new PartDomain(
                identifier,
                name,
                sku,
                description,
                brand,
                sellingPrice,
                buyPrice,
                active,
                createdAt,
                updatedAt
        );
    }


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

    public boolean getActive() {
        return active;
    }
}