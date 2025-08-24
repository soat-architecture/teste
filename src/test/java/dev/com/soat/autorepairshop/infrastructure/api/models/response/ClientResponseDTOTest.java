package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClientResponseDTO Unit Tests")
class ClientResponseDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create a valid DTO and access its properties correctly")
    void shouldCreateValidDtoAndAccessProperties() {
        var dto = new ClientResponseDTO(
            1L,
            "John Doe",
            "123.456.789-00",
            "(11) 98765-4321",
            "john.doe@example.com",
            ClientStatus.ACTIVE
        );

        assertThat(dto.identifier()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("John Doe");
        assertThat(dto.document()).isEqualTo("123.456.789-00");
        assertThat(dto.phone()).isEqualTo("(11) 98765-4321");
        assertThat(dto.email()).isEqualTo("john.doe@example.com");
        assertThat(dto.status()).isEqualTo(ClientStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should have no validation violations for a perfectly valid DTO")
    void shouldHaveNoViolationsForValidDto() {
        var dto = new ClientResponseDTO(
            1L,
            "Jane Doe",
            "987.654.321-11",
            "(21) 91234-5678",
            "jane.doe@example.com",
            ClientStatus.ACTIVE
        );

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should have violation when name is null or blank")
    void shouldHaveViolationWhenNameIsBlank(String blankName) {
        var dto = new ClientResponseDTO(1L, blankName, "doc", "(11) 99999-9999", "e@e.com", ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<ClientResponseDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("O nome é obrigatório.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should have violation when document is null or blank")
    void shouldHaveViolationWhenDocumentIsBlank(String blankDocument) {
        var dto = new ClientResponseDTO(1L, "name", blankDocument, "(11) 99999-9999", "e@e.com", ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<ClientResponseDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("document");
        assertThat(violation.getMessage()).isEqualTo("O documento é obrigatório.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should have violation when phone is null or blank")
    void shouldHaveViolationWhenPhoneIsBlank(String blankPhone) {
        var dto = new ClientResponseDTO(1L, "name", "doc", blankPhone, "e@e.com", ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"11999999999", "(11)99999-9999", "11 99999-9999", "invalid-phone"})
    @DisplayName("Should have violation for invalid phone format")
    void shouldHaveViolationForInvalidPhoneFormat(String invalidPhone) {
        var dto = new ClientResponseDTO(1L, "name", "doc", invalidPhone, "e@e.com", ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertSingleViolation(violations, "phone", "Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should have violation when email is null or blank")
    void shouldHaveViolationWhenEmailIsBlank(String blankEmail) {
        var dto = new ClientResponseDTO(1L, "name", "doc", "(11) 99999-9999", blankEmail, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "user@.com", "user@domain.", "@domain.com"})
    @DisplayName("Should have violation for invalid email format")
    void shouldHaveViolationForInvalidEmailFormat(String invalidEmail) {
        var dto = new ClientResponseDTO(1L, "name", "doc", "(11) 99999-9999", invalidEmail, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientResponseDTO>> violations = validator.validate(dto);

        assertSingleViolation(violations, "email", "Email inválido.");
    }

    private void assertSingleViolation(Set<ConstraintViolation<ClientResponseDTO>> violations, String expectedField, String expectedMessage) {
        assertThat(violations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo(expectedField);
                    assertThat(violation.getMessage()).isEqualTo(expectedMessage);
                });
    }
}