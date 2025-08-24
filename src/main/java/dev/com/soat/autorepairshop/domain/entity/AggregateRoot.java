package dev.com.soat.autorepairshop.domain.entity;

import lombok.Getter;

/**
 * Base class for all Aggregate Roots in the domain model, enforcing the requirement of a unique identifier.
 * An Aggregate Root is the primary entity in a DDD (Domain-Driven Design) aggregate and serves as the entry point
 * for all operations within its boundary.
 *
 * @param <T> The type of the identifier (e.g., UUID, Long, String)
 *
 * @example
 * <pre>
 * public class OrderService extends AggregateRoot<UUID> {
 *     private List<ItemService> items;
 *     private Client client;
 *     private Status status;
 *
 *     public OrderService(UUID identifier) {
 *         this.identifier = identifier;
 *         this.items = new ArrayList<>();
 *     }
 *
 *     public void addingItem(ItemService item) {
 *         // Business rules to validation
 *         this.items.add(item);
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://martinfowler.com/bliki/DDD_Aggregate.html">DDD Aggregate Pattern</a>
 */
@Getter
public abstract class AggregateRoot<T> {
    protected T identifier;
}
