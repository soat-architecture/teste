package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.InventoryEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class InventoryEntityMapperTest {

    private Long partId;
    private InventoryDomain inventoryDomain;
    private LocalDateTime testCreatedAt;
    private LocalDateTime testUpdatedAt;

    @BeforeEach
    void setUp() {
        partId = 123L;
        testCreatedAt = LocalDateTime.of(2025, 1, 1, 9, 0, 0);
        testUpdatedAt = LocalDateTime.of(2025, 1, 1, 10, 0, 0);

        inventoryDomain = new InventoryDomain(
                partId,
                500,
                testCreatedAt,
                testUpdatedAt
        );
    }

    @Test
    @DisplayName("map should correctly transform InventoryDomain and partId to InventoryEntity")
    void map_shouldTransformDomainAndPartIdToEntityCorrectly() {
        // Given

        // When
        InventoryEntity entity = InventoryEntityMapper.map(inventoryDomain);

        // Then
        assertAll("InventoryEntity fields should be correctly mapped",
                () -> assertThat(entity).isNotNull(),
                () -> assertThat(entity.getPartId()).isEqualTo(partId),
                () -> assertThat(entity.getQuantityOnHand()).isEqualTo(inventoryDomain.getQuantityOnHand()),
                () -> assertThat(entity.getCreatedAt()).isEqualTo(inventoryDomain.getCreatedAt()),
                () -> assertThat(entity.getUpdatedAt()).isEqualTo(inventoryDomain.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("map should handle null quantityOnHand in InventoryDomain")
    void map_shouldHandleNullQuantityOnHandInDomain() {
        // Given
        InventoryDomain domainWithNullQuantity = new InventoryDomain(
                partId,
                null,
                testCreatedAt,
                testUpdatedAt
        );

        // When
        InventoryEntity entity = InventoryEntityMapper.map(domainWithNullQuantity);

        // Then
        assertAll("InventoryEntity should have null quantityOnHand when domain's is null",
                () -> assertThat(entity).isNotNull(),
                () -> assertThat(entity.getQuantityOnHand()).isNull()
        );
    }

    @Test
    @DisplayName("map should handle null createdAt in InventoryDomain")
    void map_shouldHandleNullCreatedAtInDomain() {
        // Given
        InventoryDomain domainWithNullCreatedAt = new InventoryDomain(
                partId,
                500,
                null,
                testUpdatedAt
        );

        // When
        InventoryEntity entity = InventoryEntityMapper.map(domainWithNullCreatedAt);

        // Then
        assertAll("InventoryEntity should have null createdAt when domain's is null",
                () -> assertThat(entity).isNotNull(),
                () -> assertThat(entity.getCreatedAt()).isNull()
        );
    }

    @Test
    @DisplayName("map should handle null updatedAt in InventoryDomain")
    void map_shouldHandleNullUpdatedAtInDomain() {
        // Given
        InventoryDomain domainWithNullUpdatedAt = new InventoryDomain(
                partId,
                500,
                testCreatedAt,
                null
        );

        // When
        InventoryEntity entity = InventoryEntityMapper.map(domainWithNullUpdatedAt);

        // Then
        assertAll("InventoryEntity should have null updatedAt when domain's is null",
                () -> assertThat(entity).isNotNull(),
                () -> assertThat(entity.getUpdatedAt()).isNull()
        );
    }
}