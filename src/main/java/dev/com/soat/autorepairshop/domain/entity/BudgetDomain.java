package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BudgetDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final Long serviceOrderId;
    private final Integer version;
    private final BigDecimal totalAmount;
    private BudgetStatus status;
    private final String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime evaluatedAt;

    public BudgetDomain(
            final Long identifier,
            final Long serviceOrderId,
            final Integer version,
            final BigDecimal totalAmount,
            final BudgetStatus status,
            final String notes,
            final LocalDateTime createdAt,
            final LocalDateTime evaluatedAt
    ) {
        this.identifier = identifier;
        this.serviceOrderId = serviceOrderId;
        this.version = version;
        this.totalAmount = totalAmount;
        this.status = (status != null ? status : BudgetStatus.PENDING_APPROVAL);
        this.notes = notes;
        this.createdAt = createdAt;
        this.evaluatedAt = evaluatedAt;
    }

    public static BudgetDomain create(
            final Long serviceOrderId,
            final BigDecimal totalAmount,
            final String notes
    ) {
        var createdAt = LocalDateTime.now();
        return new BudgetDomain(
                null,
                serviceOrderId,
                null,
                totalAmount,
                BudgetStatus.PENDING_APPROVAL,
                notes,
                createdAt,
                null
        );
    }

    public static BudgetDomain restore(
            final Long identifier,
            final Long serviceOrderId,
            final Integer version,
            final BigDecimal totalAmount,
            final BudgetStatus status,
            final String notes,
            final LocalDateTime createdAt,
            final LocalDateTime evaluatedAt
    ) {
        return new BudgetDomain(
                identifier,
                serviceOrderId,
                version,
                totalAmount,
                status,
                notes,
                createdAt,
                evaluatedAt
        );
    }

    public static BudgetDomain createWithVersion(
            final Long serviceOrderId,
            final BigDecimal totalAmount,
            final String notes,
            final int version,
            final LocalDateTime createdAt
    ) {
        return new BudgetDomain(
                null,
                serviceOrderId,
                version,
                totalAmount,
                BudgetStatus.PENDING_APPROVAL,
                notes,
                createdAt,
                null
        );
    }

    public static BudgetDomain newVersionFrom(
            final BudgetDomain base,
            final BigDecimal totalAmount,
            final String notes,
            final int newVersion
    ) {
        return new BudgetDomain(
                null,
                base.getServiceOrderId(),
                newVersion,
                totalAmount,
                BudgetStatus.PENDING_APPROVAL,
                notes,
                base.getCreatedAt(),
                null
        );
    }

    public void notApproved(){
        this.status = BudgetStatus.REJECTED;
        this.evaluatedAt = LocalDateTime.now();
    }

    public void approved(){
        this.status = BudgetStatus.APPROVED;
        this.evaluatedAt = LocalDateTime.now();
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
        return evaluatedAt;
    }
}
