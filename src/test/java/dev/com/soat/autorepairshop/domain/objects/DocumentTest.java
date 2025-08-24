package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentTest {

    @Test
    @DisplayName("Should create Document with valid CPF")
    void shouldCreateDocumentWithValidCPF() {
        // Given a valid CPF
        String validCPF = "52998224725";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(validCPF));
    }

    @Test
    @DisplayName("Should create Document with valid CPF and compare")
    void shouldCreateDocumentWithValidCPFAndCompare() {
        // Given a valid CPF
        var validCPF = "52998224725";

        // When creating a document
        var document = Document.from(validCPF);

        // When creating a document and Then should not throw
        assertEquals(validCPF, document.getValue());
    }

    @Test
    @DisplayName("Should throw DomainException when CPF is invalid")
    void shouldThrowExceptionWhenCPFIsInvalid() {
        // Given an invalid CPF
        String invalidCPF = "12345678901";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(invalidCPF)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should reject CPFs with same digits")
    @ValueSource(strings = {
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555"
    })
    void shouldRejectCPFsWithSameDigits(String cpf) {
        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(cpf)
        );
    }

    @Test
    @DisplayName("Should reject CPF with invalid length")
    void shouldRejectCPFWithInvalidLength() {
        // Given a CPF with an invalid length
        String invalidLengthCPF = "123456";

        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(invalidLengthCPF)
        );
    }

    @Test
    @DisplayName("Should reject CPF with non-numeric characters")
    void shouldRejectCPFWithNonNumericCharacters() {
        // Given a CPF with non-numeric characters
        String cpfWithLetters = "1234567890a";

        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(cpfWithLetters)
        );
    }

    @Test
    @DisplayName("Should reject CPF with invalid check digits")
    void shouldRejectCPFWithInvalidCheckDigits() {
        // Given a CPF with invalid check digits
        String cpfWithInvalidCheckDigits = "52998224726";

        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(cpfWithInvalidCheckDigits)
        );
    }

    @Test
    @DisplayName("Should validate CPF with remainder < 2 for check digit 2")
    void shouldValidateCPFWithRemainderLessThanTwo() {
        // Given: a valid CPF where remainder < 2 for check digit 2
        String cpf = "63611125502";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cpf)
        );

        // Then: exception should have a correct message
        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate CPF with remainder >= 2 for check digit 2")
    void shouldValidateCPFWithRemainderGreaterOrEqualTwo() {
        // Given: a valid CPF where the remainder >= 2 for check digit 2
        String cpf = "52998224725";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(cpf));
    }

    @Test
    @DisplayName("Should reject CPF with first check digit mismatch")
    void shouldRejectCPFWithFirstCheckDigitMismatch() {
        // Given: a CPF where the first check digit is incorrect
        String cpfWithInvalidFirstCheckDigit = "52998224735";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cpfWithInvalidFirstCheckDigit)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject CPF containing non-numeric characters above '9'")
    void shouldRejectCPFWithNonNumericChar() {
        // Given: a CPF containing a character above '9'
        String cpfWithInvalidChar = "5299822:725";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cpfWithInvalidChar)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject CPF containing character below '0'")
    void shouldRejectCPFWithCharBelowZero() {
        // Given: a CPF containing a character below '0'
        String cpfWithInvalidChar = "52998/24725";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cpfWithInvalidChar)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject CPF when second check digit calculation has remainder >= 2")
    void shouldRejectCPFWhenSecondCheckDigitHasRemainderGreaterOrEqualTwo() {
        // Given: CPF that would result in a remainder >= 2 for the second check digit
        String cpf = "11144477755";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cpf)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should create Document with valid CNPJ")
    void shouldCreateDocumentWithValidCNPJ() {
        // Given a valid CNPJ
        String validCNPJ = "11222333000181";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(validCNPJ));
    }

    @Test
    @DisplayName("Should throw DomainException when CNPJ is invalid")
    void shouldThrowExceptionWhenCNPJIsInvalid() {
        // Given an invalid CNPJ
        String invalidCNPJ = "11222333000182";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(invalidCNPJ)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Should reject CNPJs with same digits")
    @ValueSource(strings = {
            "11111111111111",
            "22222222222222",
            "33333333333333",
            "44444444444444",
            "55555555555555"
    })
    void shouldRejectCNPJsWithSameDigits(String cnpj) {
        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(cnpj)
        );
    }

    @Test
    @DisplayName("Should reject CNPJ with invalid length")
    void shouldRejectCNPJWithInvalidLength() {
        // Given a CNPJ with an invalid length
        String invalidLengthCNPJ = "112223330001";

        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(invalidLengthCNPJ)
        );
    }

    @Test
    @DisplayName("Should reject CNPJ with non-numeric characters")
    void shouldRejectCNPJWithNonNumericCharacters() {
        // Given a CNPJ with non-numeric characters
        String cnpjWithLetters = "11222333000a81";

        // When creating a document and Then should throw an exception
        assertThrows(
                DomainException.class,
                () -> Document.from(cnpjWithLetters)
        );
    }

    @Test
    @DisplayName("Should reject CNPJ with invalid first check digit")
    void shouldRejectCNPJWithInvalidFirstCheckDigit() {
        // Given a CNPJ with an invalid first check digit
        String cnpjWithInvalidFirstDigit = "11222333000281";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cnpjWithInvalidFirstDigit)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject CNPJ with invalid second check digit")
    void shouldRejectCNPJWithInvalidSecondCheckDigit() {
        // Given a CNPJ with an invalid second check digit
        String cnpjWithInvalidSecondDigit = "11222333000182";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cnpjWithInvalidSecondDigit)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate CNPJ with remainder < 2 for both check digits")
    void shouldValidateCNPJWithRemainderLessThanTwo() {
        // Given a valid CNPJ where remainder < 2 for both check digits
        String cnpj = "27865757000102";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(cnpj));
    }

    @Test
    @DisplayName("Should validate formatted CNPJ")
    void shouldValidateFormattedCNPJ() {
        // Given a formatted valid CNPJ
        String formattedCNPJ = "11.222.333/0001-81";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(formattedCNPJ));
    }

    @Test
    @DisplayName("Should reject CNPJ containing special characters")
    void shouldRejectCNPJWithSpecialCharacters() {
        // Given a CNPJ with special characters that would be valid without the special char
        String cnpjWithSpecialChars = "11444777#000181";

        // When creating a document and Then should throw an exception
        DomainException exception = assertThrows(
                DomainException.class,
                () -> Document.from(cnpjWithSpecialChars)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate CNPJ with remainder >= 2 for both check digits")
    void shouldValidateCNPJWithRemainderGreaterOrEqualTwo() {
        // Given a valid CNPJ where the remainder >= 2 for both check digits
        String cnpj = "45997418000153";

        // When creating a document and Then should not throw
        assertDoesNotThrow(() -> Document.from(cnpj));
    }
}