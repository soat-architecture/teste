package dev.com.soat.autorepairshop.domain.annotation;

import dev.com.soat.autorepairshop.domain.validation.PasswordValidator;
import jakarta.validation.Constraint;

@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
}
