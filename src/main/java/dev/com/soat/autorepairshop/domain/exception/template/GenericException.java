package dev.com.soat.autorepairshop.domain.exception.template;

import dev.com.soat.autorepairshop.domain.exception.DomainExceptionHandler;

public class GenericException extends DomainExceptionHandler {
    public GenericException(String message) {
        super(message);
    }
}
