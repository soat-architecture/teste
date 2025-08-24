package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PartResponseDTO Validation Tests")
class PartResponseDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static PartResponseDTO validDTO() {
        return new PartResponseDTO(
                1L,
                "Pastilha de Freio",
                "PAST-001",
                "Pastilha para veículos leves",
                "Bosch",
                new BigDecimal("150.00"),
                new BigDecimal("100.00"),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Should not have any constraint violations for valid DTO")
    void shouldNotHaveViolationsWhenValid() {
        var dto = validDTO();

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should have violation when name is blank")
    void shouldHaveViolationWhenNameIsBlank(String name) {
        var dto = new PartResponseDTO(
                1L, name, "PAST-001", "desc", "brand",
                new BigDecimal("150.00"), new BigDecimal("100.00"),
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should have violation when SKU is blank")
    void shouldHaveViolationWhenSkuIsBlank(String sku) {
        var dto = new PartResponseDTO(
                1L, "Pastilha", sku, "desc", "brand",
                new BigDecimal("150.00"), new BigDecimal("100.00"),
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sku"));
    }

    @Test
    @DisplayName("Should have violation when sellingPrice is null")
    void shouldHaveViolationWhenSellingPriceIsNull() {
        var dto = new PartResponseDTO(
                1L, "Pastilha", "PAST-001", "desc", "brand",
                null, new BigDecimal("100.00"),
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sellingPrice"));
    }

    @Test
    @DisplayName("Should have violation when buyPrice is null")
    void shouldHaveViolationWhenBuyPriceIsNull() {
        var dto = new PartResponseDTO(
                1L, "Pastilha", "PAST-001", "desc", "brand",
                new BigDecimal("150.00"), null,
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("buyPrice"));
    }

    @Test
    @DisplayName("Should have violation when prices are negative")
    void shouldHaveViolationWhenPricesAreNegative() {
        var dto = new PartResponseDTO(
                1L, "Pastilha", "PAST-001", "desc", "brand",
                new BigDecimal("-10.00"), new BigDecimal("-5.00"),
                true, LocalDateTime.now(), LocalDateTime.now()
        );

        Set<ConstraintViolation<PartResponseDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("sellingPrice"));
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("buyPrice"));
    }
}
