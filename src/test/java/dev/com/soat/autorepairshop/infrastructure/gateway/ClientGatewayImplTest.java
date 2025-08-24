package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.infrastructure.repository.ClientRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientGatewayImplTest {

    @InjectMocks
    private ClientGatewayImpl gateway;

    @Mock
    private ClientRepository repository;

    private ClientDomain domain;

    private ClientEntity entity;

    @BeforeEach
    void setUp() {
        domain = new ClientDomain(
            1L,
            "Client Name",
            "52998224725",
            "(11) 99999-9999",
            "client@example.com",
            ClientStatus.ACTIVE,
            LocalDateTime.now(),
            LocalDateTime.now()
        );

        entity = new ClientEntity();
        entity.setId(domain.getIdentifier());
        entity.setName(domain.getName());
        entity.setDocument(domain.getUnformattedDocument());
        entity.setPhone(domain.getPhone());
        entity.setEmail(domain.getEmail());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    @Test
    void testSave() {
        when(repository.save(any(ClientEntity.class))).thenReturn(entity);

        ClientDomain savedClient = gateway.save(domain);

        assertNotNull(savedClient);
        assertEquals(domain.getName(), savedClient.getName());
        verify(repository).save(any(ClientEntity.class));
    }

    @Test
    void testUpdate() {
        when(repository.save(any(ClientEntity.class))).thenReturn(entity);

        ClientDomain updatedClient = gateway.update(domain, domain);

        assertNotNull(updatedClient);
        assertEquals(domain.getName(), updatedClient.getName());
        verify(repository).save(any(ClientEntity.class));
    }

    @Test
    void testFindByDocument() {
        when(repository.findByDocument(any(String.class))).thenReturn(entity);

        ClientDomain foundClient = gateway.findByDocument("12345678909");

        assertNotNull(foundClient);
        assertEquals(domain.getUnformattedDocument(), foundClient.getUnformattedDocument());
        verify(repository).findByDocument("12345678909");
    }

    @Test
    void testDelete() {
        String document = "123.456.789-09";
        String cleanedDocument = "12345678909";

        gateway.delete(document);

        verify(repository).softDeleteByDocument(cleanedDocument);
    }

    @Test
    void givenValidConstraintsWhenFindByConstraintsThenReturnClient() {
        when(repository.findByConstraints("52998224725", "client@example.com", "(11) 99999-9999")).thenReturn(entity);

        ClientDomain client = gateway.findByConstraints("52998224725", "client@example.com", "(11) 99999-9999");

        assertNotNull(client);
        assertEquals(domain.getName(), client.getName());
        verify(repository).findByConstraints("52998224725", "client@example.com", "(11) 99999-9999");
    }

    @Test
    void givenInvalidConstraintsWhenFindByConstraintsThenReturnNull() {
        when(repository.findByConstraints("36710135080", "client@example.com", "(11) 99999-9999")).thenReturn(null);

        ClientDomain client = gateway.findByConstraints("36710135080", "client@example.com", "(11) 99999-9999");

        assertNull(client);
        verify(repository).findByConstraints("36710135080", "client@example.com", "(11) 99999-9999");
    }
}