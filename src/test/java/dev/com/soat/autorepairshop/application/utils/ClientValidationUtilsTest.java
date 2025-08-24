package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ClientValidationUtilsTest {

    @Spy
    @InjectMocks
    private ClientValidationUtils clientValidationUtils;

    @Mock
    private ClientGateway clientGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateNewClientByDocumentException() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        ClientDomain clientDomain = new ClientDomain(1L, "Teste", document, "(11) 97653-2346",
                "teste@teste.com", ClientStatus.ACTIVE, null, null);

        when(clientGateway.findByDocument(document)).thenReturn(clientDomain);

        ConflictException conflictException = assertThrows(ConflictException.class,
                () -> clientValidationUtils.validateNewClientByDocument(document));

        assertEquals("client.already.exists", conflictException.getMessage());
    }

    @Test
    void testValidateNewClientByDocument() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        assertDoesNotThrow(() -> clientValidationUtils.validateNewClientByDocument(document));

        verify(clientGateway).findByDocument(document);
    }

    @Test
    void testValidateClientExistenceByDocument(){
        String document = Document.cleanDocument("11.222.333/0001-81");

        ClientDomain clientDomain = new ClientDomain(1L, "Teste", document, "(11) 97653-2346",
                "teste@teste.com", ClientStatus.ACTIVE, null, null);

        when(clientGateway.findByDocument(document)).thenReturn(clientDomain);

        ClientDomain result = clientValidationUtils.validateClientExistenceByDocument(document);

        assertEquals(clientDomain, result);
        verify(clientGateway).findByDocument(document);
    }

    @Test
    void testValidateClientExistenceByDocumentException() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        NotFoundException notFoundException = assertThrows(NotFoundException.class, () -> clientValidationUtils.validateClientExistenceByDocument(document));

        assertEquals("client.does.not.exists", notFoundException.getMessage());
        verify(clientGateway).findByDocument(document);
    }

    @Test
    void shouldThrowAnExceptionWhenClientIsSoftDeleted() {
        String document = Document.cleanDocument("11.222.333/0001-81");

        ClientDomain clientDomain = new ClientDomain(1L, "Teste", document, "(11) 97653-2346",
                "email@email.com", ClientStatus.DELETED, null, null);

        when(clientGateway.findByDocument(document)).thenReturn(clientDomain);

        DomainException domainException = assertThrows(DomainException.class,
                () -> clientValidationUtils.validateClientExistenceByDocument(document));

        assertEquals("client.exists.but.deleted", domainException.getMessage());
    }
}