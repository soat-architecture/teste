package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
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
class CreateServiceUseCaseTest {

    @Mock
    private ServiceGateway serviceGateway;

    @InjectMocks
    private CreateServiceUseCase createServiceUseCase;

    private ServiceInputDTO serviceInputDTO;
    private ServiceDomain inputDomain;
    private ServiceDomain savedDomain; // Still relevant if gateway.save returns it, though use case no longer uses it.

    private MockedStatic<ServiceApplicationMapper> mockedServiceApplicationMapper;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        serviceInputDTO = new ServiceInputDTO(
                "New Service A",
                "Description for new service A",
                new BigDecimal("25.50")
        );

        inputDomain = new ServiceDomain(
                null,
                serviceInputDTO.name(),
                serviceInputDTO.description(),
                serviceInputDTO.basePrice(),
                null,
                null
        );

        savedDomain = new ServiceDomain(
                1L,
                serviceInputDTO.name(),
                serviceInputDTO.description(),
                serviceInputDTO.basePrice(),
                now.minusSeconds(10),
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

    // --- REFACTORED TEST METHOD ---
    @Test
    @DisplayName("Should create a new service successfully when valid input is provided")
    void execute_shouldCreateServiceSuccessfully() {
        // Given
        // 1. Input DTO is mapped to a domain object
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenReturn(inputDomain);
        // 2. ServiceGateway saves the domain and returns the saved domain (with ID)
        // Assuming serviceGateway.save returns the saved domain, as is typical for repositories.
        when(serviceGateway.save(inputDomain)).thenReturn(savedDomain);

        // When
        // The method is now void, so we assert that no exception is thrown
        assertDoesNotThrow(() -> createServiceUseCase.execute(serviceInputDTO));

        // Then
        // Verify that the mapper's toDomain was called once with the input DTO
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        // Verify that the gateway's save method was called once with the input domain
        verify(serviceGateway, times(1)).save(inputDomain);
        // Verify that the mapper's toDTO was NOT called, as the use case no longer returns a DTO
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    // --- Existing Test Methods (from previous responses) ---

    @Test
    @DisplayName("Should throw DomainException when service input is null")
    void execute_shouldThrowDomainExceptionWhenServiceInputIsNull() {
        // Given
        ServiceInputDTO nullInput = null;

        // When / Then
        DomainException thrown = assertThrows(DomainException.class, () -> {
            createServiceUseCase.execute(nullInput);
        });

        assertEquals("Service input data must be provided", thrown.getMessage());

        mockedServiceApplicationMapper.verifyNoInteractions();
        verifyNoInteractions(serviceGateway);
    }

    @Test
    @DisplayName("Should handle gateway.save throwing an exception")
    void execute_shouldHandleGatewaySaveException() {
        // Given
        RuntimeException gatewayException = new RuntimeException("Database save failed");
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenReturn(inputDomain);
        when(serviceGateway.save(inputDomain)).thenThrow(gatewayException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            createServiceUseCase.execute(serviceInputDTO);
        });

        assertEquals(gatewayException, thrown);

        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        verify(serviceGateway, times(1)).save(inputDomain);
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should handle mapper.toDomain throwing an exception")
    void execute_shouldHandleToDomainMapperException() {
        // Given
        RuntimeException mapperException = new RuntimeException("Invalid DTO format");
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDomain(serviceInputDTO))
                .thenThrow(mapperException);

        // When / Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            createServiceUseCase.execute(serviceInputDTO);
        });

        assertEquals(mapperException, thrown);

        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDomain(serviceInputDTO), times(1));
        verifyNoInteractions(serviceGateway);
    }
}