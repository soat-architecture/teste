package dev.com.soat.autorepairshop.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PartDomainTest {

    private static final String VALID_NAME = "Filtro de Ar";
    private static final String VALID_SKU = "SKU-123";
    private static final String VALID_DESCRIPTION = "Filtro esportivo de alta performance";
    private static final String VALID_BRAND = "K&N";
    private static final BigDecimal VALID_SELLING_PRICE = new BigDecimal("120.00");
    private static final BigDecimal VALID_BUY_PRICE = new BigDecimal("80.00");

    @Nested
    @DisplayName("Create Part Tests")
    class CreatePartTests {

        @Test
        @DisplayName("Should create part successfully with valid data")
        void shouldCreatePartSuccessfully() {
            // When
            PartDomain part = PartDomain.create(
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    VALID_BUY_PRICE
            );

            // Then
            assertNotNull(part);
            assertNull(part.getIdentifier());
            assertEquals(VALID_NAME, part.getName());
            assertEquals(VALID_SKU, part.getSku());
            assertEquals(VALID_DESCRIPTION, part.getDescription());
            assertEquals(VALID_BRAND, part.getBrand());
            assertEquals(VALID_SELLING_PRICE, part.getSellingPrice());
            assertEquals(VALID_BUY_PRICE, part.getBuyPrice());
            assertTrue(part.getActive());
            assertNotNull(part.getCreatedAt());
            assertNull(part.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Restore Part Tests")
    class RestorePartTests {

        @Test
        @DisplayName("Should restore part successfully with valid data")
        void shouldRestorePartSuccessfully() {
            // Given
            Long id = 1L;
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            // When
            PartDomain part = PartDomain.restore(
                    id,
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    VALID_BUY_PRICE,
                    true,
                    createdAt,
                    updatedAt
            );

            // Then
            assertNotNull(part);
            assertEquals(id, part.getIdentifier());
            assertEquals(VALID_NAME, part.getName());
            assertEquals(VALID_SKU, part.getSku());
            assertEquals(VALID_DESCRIPTION, part.getDescription());
            assertEquals(VALID_BRAND, part.getBrand());
            assertEquals(VALID_SELLING_PRICE, part.getSellingPrice());
            assertEquals(VALID_BUY_PRICE, part.getBuyPrice());
            assertTrue(part.getActive());
            assertEquals(createdAt, part.getCreatedAt());
            assertEquals(updatedAt, part.getUpdatedAt());
        }

        @Test
        @DisplayName("Should default to active when null is passed to 'active'")
        void shouldDefaultToActiveWhenNull() {
            // When
            PartDomain part = PartDomain.restore(
                    1L,
                    VALID_NAME,
                    VALID_SKU,
                    VALID_DESCRIPTION,
                    VALID_BRAND,
                    VALID_SELLING_PRICE,
                    VALID_BUY_PRICE,
                    null,
                    LocalDateTime.now().minusDays(2),
                    LocalDateTime.now()
            );

            assertTrue(part.getActive());
        }
    }
}
