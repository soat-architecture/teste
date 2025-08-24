package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import lombok.Getter;
import lombok.val;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BudgetItemDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final Long budgetId;
    private final ItemTypeEnum itemType;
    private final Long serviceId;
    private final Long partId;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final LocalDateTime createdAt;

    public BudgetItemDomain(
            final Long identifier,
            final Long budgetId,
            final ItemTypeEnum itemType,
            final Long serviceId,
            final Long partId,
            final Integer quantity,
            final BigDecimal unitPrice,
            final LocalDateTime createdAt
    ) {
        this.identifier = identifier;
        this.budgetId = budgetId;
        this.itemType = itemType;
        this.serviceId = serviceId;
        this.partId = partId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.createdAt = createdAt;
    }

    public static BudgetItemDomain create(
            final Long budgetId,
            final ItemTypeEnum itemType,
            final Long itemId,
            final Integer quantity,
            final BigDecimal unitPrice
    ) {
        Long serviceId = null;
        Long partId = null;

        if (itemType == ItemTypeEnum.PART) {
            partId = itemId;
        } else if (itemType == ItemTypeEnum.SERVICE) {
            serviceId = itemId;
        }

        return new BudgetItemDomain(
                null,
                budgetId,
                itemType,
                serviceId,
                partId,
                quantity,
                unitPrice,
                LocalDateTime.now()
        );
    }

    public static BudgetItemDomain restore(
            final Long identifier,
            final Long budgetId,
            final ItemTypeEnum itemType,
            final Long serviceId,
            final Long partId,
            final Integer quantity,
            final BigDecimal unitPrice,
            final LocalDateTime createdAt
    ) {
        return new BudgetItemDomain(
                identifier,
                budgetId,
                itemType,
                serviceId,
                partId,
                quantity,
                unitPrice,
                createdAt
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
    protected LocalDateTime getUpdatedAt() {
        return null;
    }
}
