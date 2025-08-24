package dev.com.soat.autorepairshop.domain.validation;

import dev.com.soat.autorepairshop.domain.annotation.Password;
import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator extends DomainAbstractValidator<String>
        implements ConstraintValidator<Password, String> {

    private static final String DEFAULT_UPPERCASE_REGEX = ".*[A-Z].*";
    private static final String DEFAULT_LOWERCASE_REGEX = ".*[a-z].*";
    private static final String DEFAULT_CHARACTER_REGEX = ".*\\W.*";
    private static final Integer DEFAULT_LENGTH = 8;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        return this.validate(value);
    }

    @Override
    public boolean validate(String value) {
        if(value == null) throw new ValidatorException("password.cannot.be.null");
        if(value.length() < DEFAULT_LENGTH) throw new ValidatorException("password.least.eight.characters");
        if(!value.matches(DEFAULT_UPPERCASE_REGEX)) throw new ValidatorException("password.does.not.contain.uppercase");
        if(!value.matches(DEFAULT_LOWERCASE_REGEX)) throw new ValidatorException("password.does.not.contain.lowercase");
        if(!value.matches(DEFAULT_CHARACTER_REGEX)) throw new ValidatorException("password.does.not.contain.special.character");
        return false;
    }
}
