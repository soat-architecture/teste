package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public class OrderHistoryDomain extends DomainEntity<Long>{

    private final Long identifier;
    private final Long orderId;
    private final OrderStatusType orderStatus;
    private final String notes;
    private final LocalDateTime createdAt;

    private OrderHistoryDomain(Long identifier, Long orderId, OrderStatusType orderStatus, String notes, LocalDateTime createdAt) {
        this.orderStatus = orderStatus;
        this.orderId = orderId;
        this.notes = notes;
        this.identifier = identifier;
        this.createdAt = createdAt;
    }

    public static OrderHistoryDomain create(Long orderId, OrderStatusType orderStatus, String notes) {
        LocalDateTime changedAt = LocalDateTime.now();
        return new OrderHistoryDomain(null, orderId, orderStatus, notes, changedAt);
    }

    public static   OrderHistoryDomain restore(Long identifier, Long orderId, OrderStatusType orderStatus, String notes, LocalDateTime changedAt){
        return new OrderHistoryDomain(identifier, orderId, orderStatus, notes, changedAt);
    }

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
        return this.createdAt;
    }
}