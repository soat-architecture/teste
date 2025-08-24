package dev.com.soat.autorepairshop.domain.exception.template;

import dev.com.soat.autorepairshop.domain.exception.DomainExceptionHandler;

public class NotFoundException extends DomainExceptionHandler {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object... args) {
        super(message, args);
    }
}
