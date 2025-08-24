package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClientDomainTest {

    private Long id;
    private String name;
    private String document;
    private String phone;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        id = 1L;
        name = "John Doe";
        document = "73596896002";
        phone = "(11) 9985-7324";
        email = "john.doe@example.com";
        createdAt = LocalDateTime.now().minusDays(1);
        updatedAt = LocalDateTime.now();
    }

    @Test
    @DisplayName("Should create a ClientDomain instance and correctly retrieve its properties")
    void shouldCreateClientAndRetrieveProperties() {
        ClientDomain client = new ClientDomain(id, name, document, phone, email, ClientStatus.ACTIVE, createdAt, updatedAt);

        assertNotNull(client);
        assertEquals(id, client.getIdentifier());
        assertEquals(name, client.getName());
        assertEquals(phone, client.getPhone());
        assertEquals(email, client.getEmail());
        assertEquals(createdAt, client.getCreatedAt());
        assertEquals(updatedAt, client.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return the unformatted document value")
    void shouldReturnUnformattedDocument() {
        String formattedDocument = "304.117.000-06";
        ClientDomain client = new ClientDomain(id, name, formattedDocument, phone, email, ClientStatus.ACTIVE, createdAt, updatedAt);
        String expectedUnformattedDocument = "30411700006";

        String unformatted = client.getUnformattedDocument();

        assertEquals(expectedUnformattedDocument, unformatted);
    }

    @Test
    @DisplayName("Should return the formatted document value")
    void shouldReturnFormattedDocument() {
        ClientDomain client = new ClientDomain(id, name, document, phone, email, ClientStatus.ACTIVE, createdAt, updatedAt);
        String expectedFormattedDocument = "735.968.960-02";

        String formatted = client.getFormatedDocument();

        assertEquals(expectedFormattedDocument, formatted);
    }

    @Test
    void isDeletedMethodShouldVerifyIfStatusDeletedIsDeleted() {
        ClientDomain client = new ClientDomain(id, name, document, phone, email, ClientStatus.DELETED, createdAt, updatedAt);

        assertTrue(client.isDeleted());
    }

    @Test
    void isDeletedMethodShouldVerifyIfStatusActiveIsNotDeleted() {
        ClientDomain client = new ClientDomain(id, name, document, phone, email, ClientStatus.ACTIVE, createdAt, updatedAt);

        assertFalse(client.isDeleted());
    }

    @Test
    void isDeletedMethodShouldVerifyIfStatusInactiveIsNotDeleted() {
        ClientDomain client = new ClientDomain(id, name, document, phone, email, ClientStatus.INACTIVE, createdAt, updatedAt);

        assertFalse(client.isDeleted());
    }
}