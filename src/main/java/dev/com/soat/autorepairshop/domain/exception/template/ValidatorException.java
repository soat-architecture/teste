package dev.com.soat.autorepairshop.domain.exception.template;

import dev.com.soat.autorepairshop.domain.exception.DomainExceptionHandler;

public class ValidatorException extends DomainExceptionHandler {
    public ValidatorException(String message) {
        super(message);
    }
}
