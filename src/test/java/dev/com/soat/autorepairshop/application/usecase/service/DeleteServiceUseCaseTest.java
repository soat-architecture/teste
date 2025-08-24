package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeleteServiceUseCaseTest {

    @Mock
    private ServiceGateway gateway; // Mocking the dependency

    @InjectMocks
    private DeleteServiceUseCase deleteServiceUseCase; // The class under test

    @Test
    @DisplayName("Should call gateway.delete with the correct ID when execute is called")
    void execute_shouldCallGatewayDeleteWithCorrectId() {
        // Given
        Long serviceId = 123L;

        // When
        deleteServiceUseCase.execute(serviceId);

        // Then
        // Verify that the delete method on the gateway was called exactly once with the given serviceId
        verify(gateway, times(1)).delete(serviceId);
        // We don't need to assert a return value, as the method is void.
        // The log messages are internal to the implementation and typically not tested directly.
    }

    @Test
    @DisplayName("Should handle null ID gracefully, if gateway.delete supports it")
    void execute_shouldHandleNullId() {
        // Given
        Long nullServiceId = null;

        // When
        deleteServiceUseCase.execute(nullServiceId);

        // Then
        // Verify that the delete method on the gateway was called with null
        // This test assumes your gateway.delete method can handle a null ID without crashing.
        // If it's expected to throw an exception for null, adjust this test to expect that exception.
        verify(gateway, times(1)).delete(nullServiceId);
    }
}