package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteClientUseCaseTest {

    @InjectMocks
    private DeleteClientUseCase useCase;

    @Mock
    private ClientGateway gateway;

    @Test
    void shouldDeleteClient() {
        String document = "12345678909";

        useCase.execute(document);

        verify(gateway).delete(document);
    }
}
