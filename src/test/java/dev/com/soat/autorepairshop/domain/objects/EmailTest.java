package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class EmailTest {

    @Test
    @DisplayName("Should create Email with valid email address")
    void shouldCreateEmailWithValidAddress() {
        // Given a valid email address
        String validEmail = "user@example.com";

        // When creating email and Then should not throw
        assertDoesNotThrow(() -> new Email(validEmail));
    }

    @Test
    @DisplayName("Should throw DomainException when email is empty")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        // Given an empty email
        String emptyEmail = "";

        // When creating email and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> new Email(emptyEmail)
        );

        assertEquals("email.cannot.be.null", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should create Email with valid email formats")
    @ValueSource(strings = {
            "simple@example.com",
            "very.common@example.com",
            "disposable.style.email.with+symbol@example.com",
            "other.email-with-hyphen@example.com",
            "fully-qualified-domain@example.com",
            "user.name+tag+sorting@example.com",
            "x@example.com",
            "example-indeed@strange-example.com",
            "example@s.example",
            "firstname.lastname@example.com"
    })
    void shouldCreateEmailWithValidFormats(String validEmail) {
        // When creating email and Then should not throw
        assertDoesNotThrow(() -> new Email(validEmail));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid email formats")
    @ValueSource(strings = {
            "Abc.example.com",            // No @ character
            "A@b@c@example.com",          // Multiple @ characters
            "just\"not\"right@example.com", // Quoted strings
            "this is\"not\\allowed@example.com", // Spaces
            "this\\ still\\\"not\\\\allowed@example.com", // Escaped characters
            "@example.com",               // No local part
            "user@",                      // No domain
            ".user@example.com",          // Leading dot in the local part
            "user@example",               // Missing top-level domain
            "user@@example.com"           // Consecutive @ symbols
    })
    void shouldRejectInvalidEmailFormats(String invalidEmail) {
        // When creating email and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> new Email(invalidEmail)
        );

        assertEquals("email.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should return correct email value")
    void shouldReturnCorrectEmailValue() {
        // Given a valid email address
        String validEmail = "test@example.com";
        Email email = new Email(validEmail);

        // When getting the value
        String returnedValue = email.getValue();

        // Then should return the original email
        assertEquals(validEmail, returnedValue);
    }
}