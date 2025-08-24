package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.service.CreateServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.DeleteServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.FindAllServicesPageableUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.FindServiceUseCase;
import dev.com.soat.autorepairshop.application.usecase.service.UpdateServiceUseCase;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ServiceRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ServiceResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private FindServiceUseCase findServiceUseCase;

    @Mock
    private CreateServiceUseCase createServiceUseCase;

    @Mock
    private DeleteServiceUseCase deleteServiceUseCase;

    @Mock
    private UpdateServiceUseCase updateServiceUseCase;

    @Mock
    private FindAllServicesPageableUseCase findAllServicesPageableUseCase;

    @InjectMocks
    private ServiceController controller;

    private ServiceRequestDTO request;

    @BeforeEach
    void setup() {
        request = new ServiceRequestDTO("Troca de óleo", "Troca completa do óleo de motor", BigDecimal.valueOf(150));
    }

    @Test
    void shouldCreateService() {
        controller.create(request);

        ArgumentCaptor<ServiceInputDTO> captor = ArgumentCaptor.forClass(ServiceInputDTO.class);
        verify(createServiceUseCase).execute(captor.capture());

        ServiceInputDTO input = captor.getValue();
        assertEquals("Troca de óleo", input.name());
        assertEquals("Troca completa do óleo de motor", input.description());
        assertEquals(BigDecimal.valueOf(150), input.basePrice());
    }

    @Test
    void shouldReturnServiceResponseById() {
        Long id = 1L;
        ServiceOutputDTO output = new ServiceOutputDTO(
                id,
                "Alinhamento",
                "Alinhamento das rodas dianteiras",
                BigDecimal.valueOf(100),
                LocalDateTime.of(2024, 1, 10, 12, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0)
        );

        when(findServiceUseCase.execute(id)).thenReturn(output);

        DefaultResponseDTO<ServiceResponseDTO> response = controller.find(id);

        assertEquals("Alinhamento", response.data().name());
        assertEquals(BigDecimal.valueOf(100), response.data().basePrice());
        verify(findServiceUseCase).execute(id);
    }

    @Test
    void shouldUpdateService() {
        Long id = 2L;

        controller.update(id, request);

        ArgumentCaptor<ServiceInputDTO> captor = ArgumentCaptor.forClass(ServiceInputDTO.class);
        verify(updateServiceUseCase).execute(eq(id), captor.capture());

        ServiceInputDTO input = captor.getValue();
        assertEquals("Troca de óleo", input.name());
        assertEquals("Troca completa do óleo de motor", input.description());
        assertEquals(BigDecimal.valueOf(150), input.basePrice());
    }

    @Test
    void shouldDeleteService() {
        Long id = 3L;

        controller.delete(id);

        verify(deleteServiceUseCase).execute(id);
    }

    @Test
    void shouldReturnPaginatedServices() {
        // Arrange
        var pageNumber = 0;
        var pageSize = 10;
        var sortBy = "name";
        var direction = "asc";

        var pageInfo = getServiceOutputDTOPageInfoGenericUtils(pageNumber, pageSize);

        when(findAllServicesPageableUseCase.execute(pageNumber, pageSize, sortBy, direction))
                .thenReturn(pageInfo);

        // Act
        DefaultResponseDTO<PageInfoGenericUtils<ServiceResponseDTO>> response =
                controller.findAllPaginated(pageNumber, pageSize, sortBy, direction);

        // Assert
        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(2, response.data().content().size());
        assertEquals(pageNumber, response.data().pageNumber());
        assertEquals(pageSize, response.data().pageSize());
        assertEquals(2L, response.data().totalElements());
        assertEquals(1, response.data().totalPages());

        var firstService = response.data().content().getFirst();
        assertEquals("Alinhamento", firstService.name());
        assertEquals(BigDecimal.valueOf(100), firstService.basePrice());

        var secondService = response.data().content().get(1);
        assertEquals("Balanceamento", secondService.name());
        assertEquals(BigDecimal.valueOf(80), secondService.basePrice());

        verify(findAllServicesPageableUseCase).execute(pageNumber, pageSize, sortBy, direction);
    }

    private static PageInfoGenericUtils<ServiceOutputDTO> getServiceOutputDTOPageInfoGenericUtils(int pageNumber, int pageSize) {
        var service1 = new ServiceOutputDTO(
                1L,
                "Alinhamento",
                "Alinhamento das rodas dianteiras",
                BigDecimal.valueOf(100),
                LocalDateTime.of(2024, 1, 10, 12, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0)
        );

        var service2 = new ServiceOutputDTO(
                2L,
                "Balanceamento",
                "Balanceamento das rodas",
                BigDecimal.valueOf(80),
                LocalDateTime.of(2024, 1, 10, 12, 0),
                LocalDateTime.of(2024, 1, 15, 12, 0)
        );

        var serviceList = List.of(service1, service2);
        return new PageInfoGenericUtils<>(
                serviceList,
                pageNumber,
                pageSize,
                2L,
                1
        );
    }
}
