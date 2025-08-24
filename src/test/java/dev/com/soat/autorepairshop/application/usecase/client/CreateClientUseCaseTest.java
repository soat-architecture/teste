package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.ClientMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

    @Mock
    private ClientGateway gateway;

    @InjectMocks
    private CreateClientUseCase useCase;

    @Test
    @DisplayName("Should create a client successfully when document does not exist.")
    void givenValidClientWhenExecuteThenCreateClientSuccessfully() {
        final var input = new ClientInputDTO(1L, "John Doe", "07975278009", "(47) 99874-6473", "john.doe@example.com", ClientStatus.ACTIVE);
        final var domain = ClientMapper.map(input);

        when(gateway.findByConstraints(input.unformattedDocument(), input.email(), input.phone())).thenReturn(null);
        when(gateway.save(any(ClientDomain.class))).thenReturn(domain);
        useCase.execute(input);

        verify(gateway, times(1)).findByConstraints(input.unformattedDocument(), input.email(), input.phone());
        verify(gateway, times(1)).save(any(ClientDomain.class));
    }

    @Test
    @DisplayName("Should throw exception when client with document already exists.")
    void givenExistingClientDocumentWhenExecuteThenThrowDomainException() {
        final var input = new ClientInputDTO(1L, "John Doe", "07975278009", "(47) 99874-6473", "john.doe@example.com", ClientStatus.ACTIVE);
        final var existingClient = ClientMapper.map(input);

        when(gateway.findByConstraints(input.unformattedDocument(), input.email(), input.phone())).thenReturn(existingClient);

        assertThrows(DomainException.class, () -> useCase.execute(input));
        verify(gateway, times(1)).findByConstraints(input.unformattedDocument(), input.email(), input.phone());
        verify(gateway, never()).save(any());
    }

}