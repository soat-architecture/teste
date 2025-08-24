package dev.com.soat.autorepairshop.infrastructure.api.models.request;

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

@DisplayName("ClientRequestDTO Validation Tests")
class ClientRequestDTOTest {

    private static Validator validator;

    private static final String VALID_NAME = "John Doe";
    private static final String VALID_DOCUMENT = "725.659.390-27";
    private static final String VALID_PHONE = "(44) 92129-2267";
    private static final String VALID_EMAIL = "john.doe@example.com";

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should have no violations when DTO is valid")
    void shouldHaveNoViolationsWhenDtoIsValid() {
        var dto = new ClientRequestDTO(
            VALID_NAME,
            VALID_DOCUMENT,
            VALID_PHONE,
            VALID_EMAIL,
            ClientStatus.ACTIVE
        );

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    @DisplayName("Should have violation when name is blank")
    void shouldHaveViolationWhenNameIsBlank(String blankName) {
        var dto = new ClientRequestDTO(blankName, VALID_DOCUMENT, VALID_PHONE, VALID_EMAIL, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        ConstraintViolation<ClientRequestDTO> violation = violations.iterator().next();
        assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
        assertThat(violation.getMessage()).isEqualTo("O nome é obrigatório.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("Should have violation when document is blank")
    void shouldHaveViolationWhenDocumentIsBlank(String blankDocument) {
        var dto = new ClientRequestDTO(VALID_NAME, blankDocument, VALID_PHONE, VALID_EMAIL, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("O documento é obrigatório.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("Should have violation for blank phone")
    void shouldHaveViolationWhenPhoneIsBlank(String blankPhone) {
        var dto = new ClientRequestDTO(VALID_NAME, VALID_DOCUMENT, blankPhone, VALID_EMAIL, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"11999999999", "(11)99999-9999", "99999-9999", "(AA) 99999-9999"})
    @DisplayName("Should have violation for invalid phone format")
    void shouldHaveViolationWhenPhoneIsInvalid(String invalidPhone) {
        var dto = new ClientRequestDTO(VALID_NAME, VALID_DOCUMENT, invalidPhone, VALID_EMAIL, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  "})
    @DisplayName("Should have violation for blank email")
    void shouldHaveViolationWhenEmailIsBlank(String blankEmail) {
        var dto = new ClientRequestDTO(VALID_NAME, VALID_DOCUMENT, VALID_PHONE, blankEmail, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"plainaddress", "email.domain.com", "@domain.com", "email@domain@domain.com"})
    @DisplayName("Should have violation for invalid email format")
    void shouldHaveViolationWhenEmailIsInvalid(String invalidEmail) {
        var dto = new ClientRequestDTO(VALID_NAME, VALID_DOCUMENT, VALID_PHONE, invalidEmail, ClientStatus.ACTIVE);

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Email inválido.");
    }

    @Test
    @DisplayName("Should have no violations when status is null")
    void shouldHaveNoViolationsWhenStatusIsNull() {
        var dto = new ClientRequestDTO(
            VALID_NAME,
            VALID_DOCUMENT,
            VALID_PHONE,
            VALID_EMAIL,
            null
        );

        Set<ConstraintViolation<ClientRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }
}