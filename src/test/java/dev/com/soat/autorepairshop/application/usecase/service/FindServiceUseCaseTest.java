package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import org.junit.jupiter.api.AfterEach;
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
class FindServiceUseCaseTest {

    @Mock
    private ServiceGateway gateway;

    @InjectMocks
    private FindServiceUseCase findServiceUseCase;

    private MockedStatic<ServiceApplicationMapper> mockedServiceApplicationMapper;

    private Long serviceId;
    private ServiceDomain serviceDomain;
    private ServiceOutputDTO serviceOutputDTO;

    @BeforeEach
    void setUp() {
        serviceId = 1L;
        LocalDateTime now = LocalDateTime.now().withNano(0);

        serviceDomain = new ServiceDomain(
                serviceId,
                "Service Test",
                "Description of Service Test",
                new BigDecimal("50.00"),
                now.minusHours(1),
                now
        );

        serviceOutputDTO = new ServiceOutputDTO(
                serviceId,
                "Service Test",
                "Description of Service Test",
                new BigDecimal("50.00"),
                now.minusHours(1),
                now
        );


        mockedServiceApplicationMapper = mockStatic(ServiceApplicationMapper.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedServiceApplicationMapper != null) {
            mockedServiceApplicationMapper.close();
        }
    }

    @Test
    @DisplayName("Should return ServiceOutputDTO when service is found")
    void execute_shouldReturnServiceOutputDTOWhenServiceIsFound() {
        // Given
        when(gateway.findById(serviceId)).thenReturn(serviceDomain);

        
        mockedServiceApplicationMapper.when(() -> ServiceApplicationMapper.toDTO(serviceDomain)).thenReturn(serviceOutputDTO);

        // When
        ServiceOutputDTO result = findServiceUseCase.execute(serviceId);

        // Then
        assertNotNull(result);
        assertEquals(serviceOutputDTO.identifier(), result.identifier());
        assertEquals(serviceOutputDTO.name(), result.name());
        assertEquals(serviceOutputDTO.description(), result.description());
        assertEquals(serviceOutputDTO.basePrice(), result.basePrice());

        // Verify interactions
        verify(gateway, times(1)).findById(serviceId);
        
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(serviceDomain), times(1));
    }

    @Test
    @DisplayName("Should throw NotFoundException when service is not found")
    void execute_shouldThrowNotFoundExceptionWhenServiceIsNotFound() {
        // Given
        when(gateway.findById(serviceId)).thenReturn(null);

        // When / Then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            findServiceUseCase.execute(serviceId);
        });

        
        assertEquals("Service not found.", thrown.getMessage());
        
        verify(gateway, times(1)).findById(serviceId);
        
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

    @Test
    @DisplayName("Should handle null ID by gateway gracefully, throwing NotFoundException if gateway returns null")
    void execute_shouldHandleNullIdGracefully() {
        // Given
        Long nullServiceId = null;
        when(gateway.findById(nullServiceId)).thenReturn(null);

        // When / Then
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            findServiceUseCase.execute(nullServiceId);
        });

        
        assertEquals("Service not found.", thrown.getMessage());
        
        verify(gateway, times(1)).findById(nullServiceId);
        
        mockedServiceApplicationMapper.verify(() -> ServiceApplicationMapper.toDTO(any(ServiceDomain.class)), never());
    }

}