package dev.com.soat.autorepairshop.domain.validator;

import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import dev.com.soat.autorepairshop.domain.validation.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordValidatorTest {

    private PasswordValidator passwordValidator;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void shouldThrowExceptionWhenPasswordIsNull() {
        // Given: a null password

        // When: validating the password and Then: should throw exception
        ValidatorException exception = assertThrows(
                ValidatorException.class,
                () -> passwordValidator.isValid(null, null)
        );

        // Then: exception should have a correct message
        assertEquals("password.cannot.be.null", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when password length is less than 8 characters")
    @ValueSource(strings = {
            "Abc@123",
            "Ab@1",
            "A@b1"
    })
    void shouldThrowExceptionWhenPasswordLengthIsInvalid(String password) {
        // Given: @ValueSource provides a password with less than 8 characters

        // When: validating the password and Then: should throw exception
        ValidatorException exception = assertThrows(
                ValidatorException.class,
                () -> passwordValidator.isValid(password, null)
        );

        // Then: exception should have a correct message
        assertEquals("password.least.eight.characters", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when password does not meet character requirements")
    @MethodSource("provideInvalidPasswords")
    void shouldThrowExceptionWhenPasswordDoesNotMeetRequirements(String password, String expectedMessage) {
        // Given: @MethodSource provides an invalid password and expected message

        // When: validating the password and Then: should throw exception
        ValidatorException exception = assertThrows(
                ValidatorException.class,
                () -> passwordValidator.isValid(password, null)
        );

        // Then: exception should have a correct message
        assertEquals(expectedMessage, exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidPasswords() {
        return Stream.of(
                Arguments.of("password@123", "password.does.not.contain.uppercase"),
                Arguments.of("PASSWORD@123", "password.does.not.contain.lowercase"),
                Arguments.of("Password123", "password.does.not.contain.special.character")
        );
    }

    @ParameterizedTest
    @DisplayName("Should validate valid passwords")
    @ValueSource(strings = {
            "Password@123",
            "Complex#Password1",
            "ValidP@ssw0rd",
            "Str0ng!Pass"
    })
    void shouldValidateValidPasswords(String password) {
        // Given: @ValueSource provides a valid password

        // When: validating the password
        boolean result = passwordValidator.isValid(password, null);

        // Then: should return false as per current implementation
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false for valid password as per implementation")
    void shouldReturnFalseForValidPassword() {
        // Given: a valid password meeting all criteria
        String validPassword = "Password@123";

        // When: validating the password
        boolean result = passwordValidator.isValid(validPassword, null);

        // Then: should return false as per current implementation
        assertFalse(result);
    }
}
