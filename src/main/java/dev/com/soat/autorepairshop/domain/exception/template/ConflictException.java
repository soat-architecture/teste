package dev.com.soat.autorepairshop.domain.exception.template;

import dev.com.soat.autorepairshop.domain.exception.DomainExceptionHandler;

public class ConflictException extends DomainExceptionHandler {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Object... args) {
        super(message, args);
    }
}
