package dev.com.soat.autorepairshop.shared.formatter;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfFormatterTest {

    @Test
    @DisplayName("Should format a valid 11-digit CPF string correctly")
    void shouldFormatValidCpf() {
        String unformattedCpf = "12345678901";
        String expectedFormattedCpf = "123.456.789-01";

        String result = CpfFormatter.format(unformattedCpf);

        assertEquals(expectedFormattedCpf, result);
    }

    @Test
    @DisplayName("Should strip non-digit characters and format the CPF correctly")
    void shouldStripAndFormatCpf() {
        String dirtyCpf = "123.456.789-01";
        String expectedFormattedCpf = "123.456.789-01";

        String result = CpfFormatter.format(dirtyCpf);

        assertEquals(expectedFormattedCpf, result);
    }

    @Test
    @DisplayName("Should strip various non-digit characters and format the CPF")
    void shouldStripVariousCharactersAndFormat() {
        String dirtyCpf = "abc123def456ghi789jkl01mno";
        String expectedFormattedCpf = "123.456.789-01";

        String result = CpfFormatter.format(dirtyCpf);

        assertEquals(expectedFormattedCpf, result);
    }

    @Test
    @DisplayName("Should throw DomainException for a null CPF")
    void shouldThrowExceptionForNullCpf() {
        String expectedMessage = "cpf.cannot.be.empty";

        DomainException exception = assertThrows(DomainException.class, () -> {
            CpfFormatter.format(null);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should throw DomainException for an empty or blank CPF")
    void shouldThrowExceptionForEmptyOrBlankCpf(String blankCpf) {
        String expectedMessage = "cpf.cannot.be.empty";

        DomainException exception = assertThrows(DomainException.class, () -> {
            CpfFormatter.format(blankCpf);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DomainException for a CPF with fewer than 11 digits")
    void shouldThrowExceptionForShortCpf() {
        String shortCpf = "1234567890"; // 10 digits
        String expectedMessage = "cpf.is.invalid";

        DomainException exception = assertThrows(DomainException.class, () -> {
            CpfFormatter.format(shortCpf);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw DomainException for a CPF with more than 11 digits")
    void shouldThrowExceptionForLongCpf() {
        String longCpf = "123456789012"; // 12 digits
        String expectedMessage = "cpf.is.invalid";

        DomainException exception = assertThrows(DomainException.class, () -> {
            CpfFormatter.format(longCpf);
        });

        assertEquals(expectedMessage, exception.getMessage());
    }
}