package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.objects.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindClientUseCaseTest {

    @Mock
    ClientValidationUtils clientValidationUtils;

    @InjectMocks
    FindClientUseCase findClientUseCase;

    @Test
    @DisplayName("Should return client details successfully when document is valid.")
    void givenValidDocumentWhenExecuteThenReturnClientDetails() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        ClientDomain clientDomain = new ClientDomain(1L, "Teste", document, "(11) 97653-2346", "teste@teste.com", ClientStatus.ACTIVE, null, null);

        when(clientValidationUtils.validateClientExistenceByDocument(document)).thenReturn(clientDomain);

        ClientInputDTO result = findClientUseCase.execute(document);

        assertNotNull(result);
        assertEquals("Teste", result.name());
        assertEquals(document, result.document());
        verify(clientValidationUtils, times(1)).validateClientExistenceByDocument(document);
    }

    @Test
    @DisplayName("Should throw exception when client does not exist for the given document.")
    void givenNonExistentClientDocumentWhenExecuteThenThrowException() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        when(clientValidationUtils.validateClientExistenceByDocument(document))
                .thenThrow(new DomainException("Client not found"));

        assertThrows(DomainException.class, () -> findClientUseCase.execute(document));
        verify(clientValidationUtils, times(1)).validateClientExistenceByDocument(document);
    }

}