package dev.com.soat.autorepairshop.domain.exception;

import lombok.Getter;

/**
 * Abstract base class for domain exception handling.
 * All domain-specific exceptions should extend this class.
 */
@Getter
public abstract class DomainExceptionHandler extends RuntimeException {

    private final transient Object[] args;

    /**
     * Protected constructor to create a new domain exception.
     *
     * @param message   The base error message
     */
    protected DomainExceptionHandler(String message, Object... args) {
        super(message);
        this.args = args;
    }

}
