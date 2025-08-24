package dev.com.soat.autorepairshop.shared.formatter;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CnpjFormatterTest {

    @Test
    @DisplayName("Should format a valid 14-digit CNPJ string")
    void shouldFormatValidUnformattedCnpj() {
        String unformattedCnpj = "12345678000195";
        String expectedFormattedCnpj = "12.345.678/0001-95";

        String result = CnpjFormatter.format(unformattedCnpj);

        assertEquals(expectedFormattedCnpj, result);
    }

    @Test
    @DisplayName("Should strip non-digit characters and format a valid CNPJ")
    void shouldFormatValidCnpjWithNonDigits() {
        String dirtyCnpj = "12.345.678/0001-95";
        String expectedFormattedCnpj = "12.345.678/0001-95";

        String result = CnpjFormatter.format(dirtyCnpj);

        assertEquals(expectedFormattedCnpj, result);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings =  {"   "})
    @DisplayName("Should throw DomainException for null/empty input")
    void shouldThrowExceptionForNullInput(String value) {
        DomainException exception = assertThrows(DomainException.class, () -> {
            CnpjFormatter.format(value);
        });

        assertEquals("cpf.cannot.be.empty", exception.getMessage());
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "1234567890123", //13
        "123456789012345" //15
    })
    @DisplayName("Should throw DomainException for CNPJ with less/more than 14 digits")
    void shouldThrowExceptionForShortCnpj(String value) {

        DomainException exception = assertThrows(DomainException.class, () -> {
            CnpjFormatter.format(value);
        });

        assertEquals("cnpj.is.invalid", exception.getMessage());
    }
}