package dev.com.soat.autorepairshop.domain.entity;

import java.time.LocalDateTime;

/**
 * Base class for domain entities that require identification but are not Aggregate Roots.
 * Domain entities are objects within an aggregate that have their own identity but are
 * managed by their respective Aggregate Root.
 *
 * @param <T> The type of the identifier (e.g., UUID, Long, String)
 *
 * @example
 * <pre>
 * public class ServiceItem extends DomainEntity<UUID> {
 *     private String description;
 *     private BigDecimal amount;
 *     private Status status;
 *
 *     public ServiceItem(UUID identifier, String description, BigDecimal amount) {
 *         this.identifier = identifier;
 *         this.description = description;
 *         this.amount = amount;
 *         this.status = Status.PENDING;
 *     }
 *
 *     public void changeAmount(BigDecimal newAmount) {
 *         // Entity-specific validations
 *         this.amount = newAmount;
 *     }
 * }
 * </pre>
 *
 * Key differences between DomainEntity and AggregateRoot:
 * - DomainEntity: Managed within an aggregate, with local scope
 * - AggregateRoot: Manages other entities and ensures aggregate consistency
 *
 * @see AggregateRoot
 */
public abstract class DomainEntity<T> {
    protected abstract T getIdentifier();
    protected abstract LocalDateTime getCreatedAt();
    protected abstract LocalDateTime getUpdatedAt();
}