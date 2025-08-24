package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PartRequestDTO Validation Tests")
class PartRequestDTOTest {

    private static Validator validator;

    private static final String VALID_NAME = "Pastilha de Freio";
    private static final String VALID_SKU = "PAST-FREIO-123";
    private static final String VALID_DESCRIPTION = "Pastilha dianteira para carro";
    private static final String VALID_BRAND = "Brembo";
    private static final BigDecimal VALID_SELLING_PRICE = new BigDecimal("150.00");
    private static final BigDecimal VALID_BUY_PRICE = new BigDecimal("100.00");

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should have no violations when DTO is valid")
    void shouldHaveNoViolationsWhenDtoIsValid() {
        var dto = new PartRequestDTO(
                VALID_NAME,
                VALID_SKU,
                VALID_DESCRIPTION,
                VALID_BRAND,
                VALID_SELLING_PRICE,
                VALID_BUY_PRICE
        );

        Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Nested
    class NameValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\n", "\t"})
        @DisplayName("Should have violation when name is blank")
        void shouldHaveViolationWhenNameIsBlank(String blankName) {
            var dto = new PartRequestDTO(
                    blankName,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    VALID_BUY_PRICE
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            var violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("name");
            assertThat(violation.getMessage()).isEqualTo("O nome é obrigatório.");
        }
    }

    @Nested
    class SkuValidation {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should have violation when SKU is blank")
        void shouldHaveViolationWhenSkuIsBlank(String blankSku) {
            var dto = new PartRequestDTO(
                    VALID_NAME,
                    blankSku,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    VALID_BUY_PRICE
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            var violation = violations.iterator().next();
            assertThat(violation.getPropertyPath().toString()).isEqualTo("sku");
            assertThat(violation.getMessage()).isEqualTo("O SKU é obrigatório.");
        }
    }

    @Nested
    class SellingPriceValidation {

        @Test
        @DisplayName("Should have violation when sellingPrice is null")
        void shouldHaveViolationWhenSellingPriceIsNull() {
            var dto = new PartRequestDTO(
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    null,
                    VALID_BUY_PRICE
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("O preço de venda é obrigatório.");
        }

        @Test
        @DisplayName("Should have violation when sellingPrice is zero or negative")
        void shouldHaveViolationWhenSellingPriceIsInvalid() {
            var dto = new PartRequestDTO(
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    BigDecimal.ZERO,
                    VALID_BUY_PRICE
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("O preço de venda deve ser um valor positivo.");
        }
    }

    @Nested
    class BuyPriceValidation {

        @Test
        @DisplayName("Should have violation when buyPrice is null")
        void shouldHaveViolationWhenBuyPriceIsNull() {
            var dto = new PartRequestDTO(
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    null
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("O preço de compra é obrigatório.");
        }

        @Test
        @DisplayName("Should have violation when buyPrice is zero or negative")
        void shouldHaveViolationWhenBuyPriceIsInvalid() {
            var dto = new PartRequestDTO(
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    new BigDecimal("-1.00")
            );

            Set<ConstraintViolation<PartRequestDTO>> violations = validator.validate(dto);

            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("O preço de compra deve ser um valor positivo.");
        }
    }
}
