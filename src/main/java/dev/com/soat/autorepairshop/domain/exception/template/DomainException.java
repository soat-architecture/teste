package dev.com.soat.autorepairshop.domain.exception.template;

import dev.com.soat.autorepairshop.domain.exception.DomainExceptionHandler;

public class DomainException extends DomainExceptionHandler {
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Object... args) {
        super(message, args);
    }
}
