package dev.com.soat.autorepairshop.domain.objects;

import lombok.EqualsAndHashCode;

/**
 * Base class for Value Objects in the domain.
 * Value Objects are immutable objects that don't have their own identity and are
 * characterized solely by their attributes. <br/>
 *
 *  <br/>
 *  Key characteristics of Value Objects: <br/>
 * - Immutability: Once created, their values cannot be changed <br/>
 * - Value Equality: Two Value Objects are equal if all their attributes are equal <br/>
 * - No Identity: They don't have a unique identifier <br/>
 * - Validation: Ensures the encapsulated value meets business rules <br/>
 *
 * @param <T> The type of the encapsulated value (e.g., String, Integer, CustomType)
 */
@EqualsAndHashCode
public abstract class ValueObject<T> {
    protected final T value;

    protected ValueObject(T value) {
        this.value = value;
        this.validate(value);
    }

    protected abstract T getValue();
    protected abstract void validate(T value);
}
