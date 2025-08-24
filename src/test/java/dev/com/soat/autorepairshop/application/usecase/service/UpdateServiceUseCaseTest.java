package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateServiceUseCaseTest {

    @Mock
    private ServiceGateway gateway;

    @InjectMocks
    private UpdateServiceUseCase updateServiceUseCase;

    private Long serviceId;
    private ServiceDomain existingServiceDomain;
    private ServiceInputDTO serviceInputDTO;
    private ServiceDomain domainFromInputDTO;
    private ServiceDomain updatedServiceDomain;

    private MockedStatic<ServiceApplicationMapper> mockedServiceApplicationMapper;

    @BeforeEach
    void setUp() {
        serviceId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);

        existingServiceDomain = new ServiceDomain(
                serviceId,
                "Old Service Name",
                "Old Description",
                new BigDecimal("100.00"),
                now.minusDays(7),
                now.minusDays(1)
        );

        serviceInputDTO = new ServiceInputDTO(
                "New Service Name",
                "New Description",
                new BigDecimal("120.00")
        );

        domainFromInputDTO = new ServiceDomain(
                null,
                serviceInputDTO.name(),
                serviceInputDTO.description(),
                serviceInputDTO.basePrice(),
                null,
                null
        );

        updatedServiceDomain = new ServiceDomain(
                serviceId,
                serviceInputDTO.name(),
                serviceInputDTO.description(),
                serviceInputDTO.basePrice(),
                existingServiceDomain.getCreatedAt(),
                now
        );
        mockedServiceApplicationMapper = mockStatic(ServiceApplicationMapper.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        if (mockedServiceApplicationMapper != null) {
            mockedServiceApplicationMapper.close();
        }
    }

    @Test
    @DisplayName("Should update an existing service and return ServiceOutputDTO successfully")
    void execute_shouldUpdateExistingServiceSuccessfully() { // Renamed for clarity
        // Given
        when(gateway.findById(serviceId)).thenReturn(existingServiceDomain);
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenReturn(domainFromInputDTO);
        when(gateway.update(existingServiceDomain, domainFromInputDTO)).thenReturn(updatedServiceDomain);

        // When
        assertDoesNotThrow(() -> updateServiceUseCase.execute(serviceId, serviceInputDTO));

        // Then
        verify(gateway, times(1)).findById(serviceId);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        verify(gateway, times(1)).update(existingServiceDomain, domainFromInputDTO);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should throw NotFoundException when service to update is not found")
    void execute_shouldThrowNotFoundExceptionWhenServiceIsNotFound() {
        // Given
        when(gateway.findById(serviceId)).thenReturn(null);

        // When / Then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> updateServiceUseCase.execute(serviceId, serviceInputDTO));

        assertEquals("Service not found.", thrown.getMessage());
        verify(gateway, times(1)).findById(serviceId);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(any(ServiceInputDTO.class)), never());
        verify(gateway, never()).update(any(ServiceDomain.class), any(ServiceDomain.class));
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should handle null ID gracefully, throwing NotFoundException if gateway returns null")
    void execute_shouldThrowNotFoundExceptionForNullIdIfGatewayReturnsNull() {
        // Given
        Long nullServiceId = null;
        when(gateway.findById(nullServiceId)).thenReturn(null);

        // When / Then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> updateServiceUseCase.execute(nullServiceId, serviceInputDTO));

        assertEquals("Service not found.", thrown.getMessage());
        verify(gateway, times(1)).findById(nullServiceId);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(any(ServiceInputDTO.class)), never());
        verify(gateway, never()).update(any(ServiceDomain.class), any(ServiceDomain.class));
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should handle gateway.findById throwing an exception (e.g., database error)")
    void execute_shouldHandleGatewayFindByIdException() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database connection failed during find");
        when(gateway.findById(serviceId)).thenThrow(gatewayException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> updateServiceUseCase.execute(serviceId, serviceInputDTO));

        assertEquals(gatewayException, thrown);
        verify(gateway, times(1)).findById(serviceId);
        mockedServiceApplicationMapper.verifyNoInteractions();
        verifyNoMoreInteractions(gateway);
    }

    @Test
    @DisplayName("Should handle ServiceApplicationMapper.toDomain throwing an exception")
    void execute_shouldHandleToDomainMapperException() {
        // Given
        when(gateway.findById(serviceId)).thenReturn(existingServiceDomain);
        RuntimeException mapperException = new RuntimeException("Invalid DTO format for mapping to domain");
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenThrow(mapperException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> updateServiceUseCase.execute(serviceId, serviceInputDTO));

        assertEquals(mapperException, thrown);
        verify(gateway, times(1)).findById(serviceId);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        verify(gateway, never()).update(any(ServiceDomain.class), any(ServiceDomain.class));
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should handle gateway.update throwing an exception")
    void execute_shouldHandleGatewayUpdateException() {
        // Given
        when(gateway.findById(serviceId)).thenReturn(existingServiceDomain);
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenReturn(domainFromInputDTO);
        RuntimeException updateException = new RuntimeException("Database update failed");
        when(gateway.update(existingServiceDomain, domainFromInputDTO)).thenThrow(updateException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> updateServiceUseCase.execute(serviceId, serviceInputDTO));

        assertEquals(updateException, thrown);
        verify(gateway, times(1)).findById(serviceId);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        verify(gateway, times(1)).update(existingServiceDomain, domainFromInputDTO);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }
}