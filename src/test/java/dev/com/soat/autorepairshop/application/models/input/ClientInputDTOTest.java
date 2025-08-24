package dev.com.soat.autorepairshop.application.models.input;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientInputDTOTest {

    @Test
    @DisplayName("Should return masked document successfully.")
    void givenValidDocumentWhenMaskedDocumentThenReturnMaskedValue() {
        final var clientInputDTO = new ClientInputDTO(1L, "John Doe", "58352414020", "1234567890", "john.doe@example.com", ClientStatus.ACTIVE);

        final var maskedDocument = clientInputDTO.maskedDocument();

        assertNotNull(maskedDocument);
        assertEquals("583.***.***-20", maskedDocument);
    }

    @Test
    @DisplayName("Should return unformatted document successfully.")
    void givenFormattedDocumentWhenUnformattedDocumentThenReturnUnformattedValue() {
        final var clientInputDTO = new ClientInputDTO(1L, "John Doe", "859.086.200-33", "1234567890", "john.doe@example.com", ClientStatus.ACTIVE);

        final var unformattedDocument = clientInputDTO.unformattedDocument();

        assertNotNull(unformattedDocument);
        assertEquals("85908620033", unformattedDocument);
    }

    @Test
    @DisplayName("Should handle null document when masking.")
    void givenNullDocumentWhenMaskedDocumentThenReturnNull() {
        final var clientInputDTO = new ClientInputDTO(1L, "John Doe", null, "1234567890", "john.doe@example.com", ClientStatus.ACTIVE);

        var exception = assertThrows(DomainException.class, () -> clientInputDTO.maskedDocument());
        assertEquals("document.cannot.be.null", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle null document when unformatting.")
    void givenNullDocumentWhenUnformattedDocumentThenReturnNull() {
        final var clientInputDTO = new ClientInputDTO(1L, "John Doe", null, "1234567890", "john.doe@example.com", ClientStatus.ACTIVE);


        var exception = assertThrows(DomainException.class, () -> clientInputDTO.unformattedDocument());
        assertEquals("document.cannot.be.null", exception.getMessage());
    }

}