package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ServiceRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.ServiceResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceMapperTest {

    private ServiceInputDTO serviceInputDTO;
    private ServiceRequestDTO serviceRequestDTO;
    private ServiceOutputDTO serviceOutputDTO;

    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.now().withNano(0);

        serviceInputDTO = new ServiceInputDTO(
                "Input Service Name",
                "Input Service Description",
                new BigDecimal("50.00")
        );

        serviceRequestDTO = new ServiceRequestDTO(
                "Request Service Name",
                "Request Service Description",
                new BigDecimal("75.50")
        );

        serviceOutputDTO = new ServiceOutputDTO(
                1L,
                "Output Service Name",
                "Output Service Description",
                new BigDecimal("120.00"),
                testDateTime.minusDays(5),
                testDateTime.minusHours(1)
        );
    }

    @Test
    @DisplayName("map(ServiceInputDTO) should correctly map all fields to ServiceDomain")
    void mapInputDtoToDomain_shouldMapAllFieldsCorrectly() {
        // When
        ServiceDomain domain = ServiceMapper.map(serviceInputDTO);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getIdentifier()).isNull();
        assertThat(domain.getName()).isEqualTo(serviceInputDTO.name());
        assertThat(domain.getDescription()).isEqualTo(serviceInputDTO.description());
        assertThat(domain.getBasePrice()).isEqualTo(serviceInputDTO.basePrice());

        assertThat(domain.getCreatedAt()).isNotNull();
        assertThat(domain.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("map(ServiceInputDTO) should handle null fields in ServiceInputDTO gracefully")
    void mapInputDtoToDomain_shouldHandleNullFields() {
        // Given
        ServiceInputDTO dtoWithNulls = new ServiceInputDTO(null, null, null);

        // When
        ServiceDomain domain = ServiceMapper.map(dtoWithNulls);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getName()).isNull();
        assertThat(domain.getDescription()).isNull();
        assertThat(domain.getBasePrice()).isNull();
        assertThat(domain.getIdentifier()).isNull();
        assertThat(domain.getCreatedAt()).isNotNull();
        assertThat(domain.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("map(ServiceInputDTO) should throw NullPointerException when ServiceInputDTO is null")
    void mapInputDtoToDomain_shouldThrowNullPointerExceptionForNullInput() {
        // When / Then
        assertThatThrownBy(() -> ServiceMapper.map((ServiceInputDTO) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("map(ServiceRequestDTO) should correctly map all fields to ServiceInputDTO")
    void mapRequestDtoToInputDto_shouldMapAllFieldsCorrectly() {
        // When
        ServiceInputDTO inputDTO = ServiceMapper.map(serviceRequestDTO);

        // Then
        assertThat(inputDTO).isNotNull();
        assertThat(inputDTO.name()).isEqualTo(serviceRequestDTO.name());
        assertThat(inputDTO.description()).isEqualTo(serviceRequestDTO.description());
        assertThat(inputDTO.basePrice()).isEqualTo(serviceRequestDTO.basePrice());
    }

    @Test
    @DisplayName("map(ServiceRequestDTO) should handle null fields in ServiceRequestDTO gracefully")
    void mapRequestDtoToInputDto_shouldHandleNullFields() {
        // Given
        ServiceRequestDTO requestWithNulls = new ServiceRequestDTO(null, null, null);

        // When
        ServiceInputDTO inputDTO = ServiceMapper.map(requestWithNulls);

        // Then
        assertThat(inputDTO).isNotNull();
        assertThat(inputDTO.name()).isNull();
        assertThat(inputDTO.description()).isNull();
        assertThat(inputDTO.basePrice()).isNull();
    }

    @Test
    @DisplayName("map(ServiceRequestDTO) should throw NullPointerException when ServiceRequestDTO is null")
    void mapRequestDtoToInputDto_shouldThrowNullPointerExceptionForNullRequest() {
        // When / Then
        assertThatThrownBy(() -> ServiceMapper.map((ServiceRequestDTO) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("map(ServiceOutputDTO) should correctly map all fields to ServiceResponseDTO")
    void mapOutputDtoToResponseDto_shouldMapAllFieldsCorrectly() {
        // When
        ServiceResponseDTO responseDTO = ServiceMapper.map(serviceOutputDTO);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.identifier()).isEqualTo(serviceOutputDTO.identifier());
        assertThat(responseDTO.name()).isEqualTo(serviceOutputDTO.name());
        assertThat(responseDTO.description()).isEqualTo(serviceOutputDTO.description());
        assertThat(responseDTO.basePrice()).isEqualTo(serviceOutputDTO.basePrice());
        assertThat(responseDTO.createdAt()).isEqualToIgnoringNanos(serviceOutputDTO.createdAt());
        assertThat(responseDTO.updatedAt()).isEqualToIgnoringNanos(serviceOutputDTO.updatedAt());
    }

    @Test
    @DisplayName("map(ServiceOutputDTO) should handle null fields in ServiceOutputDTO gracefully")
    void mapOutputDtoToResponseDto_shouldHandleNullFields() {
        // Given
        ServiceOutputDTO outputWithNulls = new ServiceOutputDTO(
                null, null, null, null, null, null
        );

        // When
        ServiceResponseDTO responseDTO = ServiceMapper.map(outputWithNulls);

        // Then
        assertThat(responseDTO).isNotNull();
        assertThat(responseDTO.identifier()).isNull();
        assertThat(responseDTO.name()).isNull();
        assertThat(responseDTO.description()).isNull();
        assertThat(responseDTO.basePrice()).isNull();
        assertThat(responseDTO.createdAt()).isNull();
        assertThat(responseDTO.updatedAt()).isNull();
    }
}