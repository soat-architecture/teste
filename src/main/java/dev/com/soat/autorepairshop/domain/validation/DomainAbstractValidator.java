package dev.com.soat.autorepairshop.domain.validation;

/**
 * Abstract base class for domain validation.
 * Each class that extends this validator must implement the validate method,
 * which will be used to generate validation annotations.
 *
 * @param <T> The type of the value to be validated
 */
public abstract class DomainAbstractValidator<T> {
    
    /**
     * Abstract method to validate a domain value.
     * Implementations should define their specific validation logic.
     *
     * @param value The value to be validated
     * @return true if the value is valid, false otherwise
     */
    public abstract boolean validate(T value);
}