package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceApplicationMapperTest {

    private ServiceInputDTO serviceInputDTO;
    private ServiceDomain serviceDomain;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0); // Normalize nanoseconds for consistent comparisons

        serviceInputDTO = new ServiceInputDTO(
                "Service Name from Input",
                "Service Description from Input",
                new BigDecimal("99.99")
        );

        serviceDomain = new ServiceDomain(
                10L, // Assuming an ID for the domain object
                "Service Name from Domain",
                "Service Description from Domain",
                new BigDecimal("150.00"),
                now.minusDays(2), // createdAt
                now // updatedAt
        );
    }

    // --- Tests for toDomain(ServiceInputDTO dto) ---

    @Test
    @DisplayName("toDomain should map all fields from ServiceInputDTO to ServiceDomain correctly")
    void toDomain_shouldMapAllFieldsCorrectly() {
        // When
        ServiceDomain domain = ServiceApplicationMapper.toDomain(serviceInputDTO);

        // Then
        assertThat(domain).isNotNull();
        // For 'create' method, ID, createdAt, updatedAt might be null or default,
        // so we only assert on fields passed to create.
        assertThat(domain.getName()).isEqualTo(serviceInputDTO.name());
        assertThat(domain.getDescription()).isEqualTo(serviceInputDTO.description());
        assertThat(domain.getBasePrice()).isEqualTo(serviceInputDTO.basePrice());
        // Assert that partId, createdAt, updatedAt are handled as expected by ServiceDomain.create
        // (e.g., they might be null or initialized to default values)
        assertThat(domain.getIdentifier()).isNull(); // Assuming create doesn't set ID
        assertThat(domain.getCreatedAt()).isNull(); // Assuming create doesn't set dates
        assertThat(domain.getUpdatedAt()).isNull(); // Assuming create doesn't set dates
    }

    @Test
    @DisplayName("toDomain should handle null name in ServiceInputDTO")
    void toDomain_shouldHandleNullName() {
        // Given
        ServiceInputDTO dtoWithNullName = new ServiceInputDTO(
                null,
                serviceInputDTO.description(),
                serviceInputDTO.basePrice()
        );

        // When
        ServiceDomain domain = ServiceApplicationMapper.toDomain(dtoWithNullName);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getName()).isNull();
        assertThat(domain.getDescription()).isEqualTo(dtoWithNullName.description());
    }

    @Test
    @DisplayName("toDomain should handle null description in ServiceInputDTO")
    void toDomain_shouldHandleNullDescription() {
        // Given
        ServiceInputDTO dtoWithNullDescription = new ServiceInputDTO(
                serviceInputDTO.name(),
                null,
                serviceInputDTO.basePrice()
        );

        // When
        ServiceDomain domain = ServiceApplicationMapper.toDomain(dtoWithNullDescription);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getDescription()).isNull();
        assertThat(domain.getName()).isEqualTo(dtoWithNullDescription.name());
    }

    @Test
    @DisplayName("toDomain should handle null basePrice in ServiceInputDTO")
    void toDomain_shouldHandleNullBasePrice() {
        // Given
        ServiceInputDTO dtoWithNullBasePrice = new ServiceInputDTO(
                serviceInputDTO.name(),
                serviceInputDTO.description(),
                null
        );

        // When
        ServiceDomain domain = ServiceApplicationMapper.toDomain(dtoWithNullBasePrice);

        // Then
        assertThat(domain).isNotNull();
        assertThat(domain.getBasePrice()).isNull();
        assertThat(domain.getName()).isEqualTo(dtoWithNullBasePrice.name());
    }

    @Test
    @DisplayName("toDomain should throw NullPointerException when ServiceInputDTO is null")
    void toDomain_shouldThrowNullPointerExceptionWhenInputDTOIsNull() {
        // Given
        ServiceInputDTO nullDTO = null;

        // When / Then
        assertThatThrownBy(() -> ServiceApplicationMapper.toDomain(nullDTO))
                .isInstanceOf(NullPointerException.class); // Expecting NPE when trying to access nullDTO.name() etc.
    }

    // --- Tests for toDTO(final ServiceDomain domain) ---

    @Test
    @DisplayName("toDTO should map all fields from ServiceDomain to ServiceOutputDTO correctly")
    void toDTO_shouldMapAllFieldsCorrectly() {
        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(serviceDomain);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.identifier()).isEqualTo(serviceDomain.getIdentifier());
        assertThat(dto.name()).isEqualTo(serviceDomain.getName());
        assertThat(dto.description()).isEqualTo(serviceDomain.getDescription());
        assertThat(dto.basePrice()).isEqualTo(serviceDomain.getBasePrice());
        assertThat(dto.createdAt()).isEqualToIgnoringNanos(serviceDomain.getCreatedAt());
        assertThat(dto.updatedAt()).isEqualToIgnoringNanos(serviceDomain.getUpdatedAt());
    }

    @Test
    @DisplayName("toDTO should handle null partId in ServiceDomain")
    void toDTO_shouldHandleNullIdentifier() {
        // Given
        ServiceDomain domainWithNullId = new ServiceDomain(
                null, // Null ID
                serviceDomain.getName(),
                serviceDomain.getDescription(),
                serviceDomain.getBasePrice(),
                serviceDomain.getCreatedAt(),
                serviceDomain.getUpdatedAt()
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullId);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.identifier()).isNull();
        assertThat(dto.name()).isEqualTo(domainWithNullId.getName());
    }

    @Test
    @DisplayName("toDTO should handle null name in ServiceDomain")
    void toDTO_shouldHandleNullNameInDomain() {
        // Given
        ServiceDomain domainWithNullName = new ServiceDomain(
                serviceDomain.getIdentifier(),
                null, // Null name
                serviceDomain.getDescription(),
                serviceDomain.getBasePrice(),
                serviceDomain.getCreatedAt(),
                serviceDomain.getUpdatedAt()
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullName);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.name()).isNull();
        assertThat(dto.identifier()).isEqualTo(domainWithNullName.getIdentifier());
    }

    @Test
    @DisplayName("toDTO should handle null description in ServiceDomain")
    void toDTO_shouldHandleNullDescriptionInDomain() {
        // Given
        ServiceDomain domainWithNullDescription = new ServiceDomain(
                serviceDomain.getIdentifier(),
                serviceDomain.getName(),
                null, // Null description
                serviceDomain.getBasePrice(),
                serviceDomain.getCreatedAt(),
                serviceDomain.getUpdatedAt()
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullDescription);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.description()).isNull();
        assertThat(dto.name()).isEqualTo(domainWithNullDescription.getName());
    }

    @Test
    @DisplayName("toDTO should handle null basePrice in ServiceDomain")
    void toDTO_shouldHandleNullBasePriceInDomain() {
        // Given
        ServiceDomain domainWithNullBasePrice = new ServiceDomain(
                serviceDomain.getIdentifier(),
                serviceDomain.getName(),
                serviceDomain.getDescription(),
                null, // Null basePrice
                serviceDomain.getCreatedAt(),
                serviceDomain.getUpdatedAt()
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullBasePrice);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.basePrice()).isNull();
        assertThat(dto.name()).isEqualTo(domainWithNullBasePrice.getName());
    }

    @Test
    @DisplayName("toDTO should handle null createdAt in ServiceDomain")
    void toDTO_shouldHandleNullCreatedAtInDomain() {
        // Given
        ServiceDomain domainWithNullCreatedAt = new ServiceDomain(
                serviceDomain.getIdentifier(),
                serviceDomain.getName(),
                serviceDomain.getDescription(),
                serviceDomain.getBasePrice(),
                null, // Null createdAt
                serviceDomain.getUpdatedAt()
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullCreatedAt);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.createdAt()).isNull();
        assertThat(dto.name()).isEqualTo(domainWithNullCreatedAt.getName());
    }

    @Test
    @DisplayName("toDTO should handle null updatedAt in ServiceDomain")
    void toDTO_shouldHandleNullUpdatedAtInDomain() {
        // Given
        ServiceDomain domainWithNullUpdatedAt = new ServiceDomain(
                serviceDomain.getIdentifier(),
                serviceDomain.getName(),
                serviceDomain.getDescription(),
                serviceDomain.getBasePrice(),
                serviceDomain.getCreatedAt(),
                null // Null updatedAt
        );

        // When
        ServiceOutputDTO dto = ServiceApplicationMapper.toDTO(domainWithNullUpdatedAt);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.updatedAt()).isNull();
        assertThat(dto.name()).isEqualTo(domainWithNullUpdatedAt.getName());
    }

    @Test
    @DisplayName("toDTO should throw NullPointerException when ServiceDomain is null")
    void toDTO_shouldThrowNullPointerExceptionWhenDomainIsNull() {
        // Given
        ServiceDomain nullDomain = null;

        // When / Then
        assertThatThrownBy(() -> ServiceApplicationMapper.toDTO(nullDomain))
                .isInstanceOf(NullPointerException.class); // Expecting NPE when trying to access nullDomain.getIdentifier() etc.
    }
}