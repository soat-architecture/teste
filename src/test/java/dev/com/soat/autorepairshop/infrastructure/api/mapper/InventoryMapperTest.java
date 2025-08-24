package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.InventoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class InventoryMapperTest {

    private InventoryEntity inventoryEntity;

    @BeforeEach
    void setUp() {
        LocalDateTime testCreatedAt = LocalDateTime.of(2025, 7, 20, 9, 0, 0);
        LocalDateTime testUpdatedAt = LocalDateTime.of(2025, 7, 24, 11, 30, 0);

        inventoryEntity = new InventoryEntity();
        inventoryEntity.setPartId(2L);
        inventoryEntity.setQuantityOnHand(500);
        inventoryEntity.setCreatedAt(testCreatedAt);
        inventoryEntity.setUpdatedAt(testUpdatedAt);
    }

    @Test
    @DisplayName("map(InventoryOutputDTO) should correctly map quantityOnHand to InventoryDomain")
    void mapOutputDtoToDomain_shouldMapQuantityOnHandCorrectly() {
        // When
        InventoryDomain domain = InventoryMapper.map(inventoryEntity);

        // Then
        assertAll("InventoryDomain from OutputDTO should be correctly mapped",
                () -> assertThat(domain).isNotNull()
        );
    }

    @Test
    @DisplayName("map(InventoryEntity) should correctly map all fields to InventoryDomain")
    void mapEntityToDomain_shouldMapAllFieldsCorrectly() {
        // When
        InventoryDomain domain = InventoryMapper.map(inventoryEntity);

        // Then
        assertAll("InventoryDomain from Entity should be correctly mapped",
                () -> assertThat(domain).isNotNull(),
                () -> assertThat(domain.getIdentifier()).isEqualTo(inventoryEntity.getPartId()), // Asserting getPartId()
                () -> assertThat(domain.getQuantityOnHand()).isEqualTo(inventoryEntity.getQuantityOnHand()),
                () -> assertThat(domain.getCreatedAt()).isEqualTo(inventoryEntity.getCreatedAt()),
                () -> assertThat(domain.getUpdatedAt()).isEqualTo(inventoryEntity.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("map(InventoryEntity) should handle null fields in InventoryEntity")
    void mapEntityToDomain_shouldHandleNullFields() {
        // Given
        InventoryEntity entityWithNulls = new InventoryEntity();
        entityWithNulls.setPartId(null);
        entityWithNulls.setQuantityOnHand(null);
        entityWithNulls.setCreatedAt(null);
        entityWithNulls.setUpdatedAt(null);

        // When
        InventoryDomain domain = InventoryMapper.map(entityWithNulls);

        // Then
        assertAll("InventoryDomain should handle nulls from entity",
                () -> assertThat(domain).isNotNull(),
                () -> assertThat(domain.getIdentifier()).isNull(),
                () -> assertThat(domain.getQuantityOnHand()).isNull(),
                () -> assertThat(domain.getCreatedAt()).isNull(),
                () -> assertThat(domain.getUpdatedAt()).isNull()
        );
    }
}