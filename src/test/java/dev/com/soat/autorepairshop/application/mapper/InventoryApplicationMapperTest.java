package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryApplicationMapperTest {

    private InventoryRequestDTO inventoryRequestDTO;
    private InventoryOutputDTO inventoryOutputDTO;

    @BeforeEach
    void setUp() {
        inventoryRequestDTO = new InventoryRequestDTO(
                1L,
                100
        );

        inventoryOutputDTO = new InventoryOutputDTO(
                2L,
                250
        );
    }

    @Test
    @DisplayName("map(InventoryRequestDTO) deve mapear todos os campos corretamente para InventoryOutputDTO")
    void mapRequestDtoToOutputDto_shouldMapAllFieldsCorrectly() {
        // Given
        InventoryInputDTO result = InventoryApplicationMapper.map(inventoryRequestDTO);
        // When && Then
        assertThat(result).isNotNull();
        assertThat(result.partId()).isEqualTo(inventoryRequestDTO.partId());
        assertThat(result.quantityChanged()).isEqualTo(inventoryRequestDTO.quantityOnHand());
    }

    @Test
    @DisplayName("map(InventoryRequestDTO) deve lidar com partId nulo em InventoryRequestDTO")
    void mapRequestDtoToOutputDto_shouldHandleNullPartId() {
        // Given
        InventoryRequestDTO requestWithNullPartId = new InventoryRequestDTO(null, 10);

        // When
        InventoryInputDTO result = InventoryApplicationMapper.map(requestWithNullPartId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.partId()).isNull();
        assertThat(result.quantityChanged()).isEqualTo(10);
    }

    @Test
    @DisplayName("map(InventoryRequestDTO) deve lançar NullPointerException When InventoryRequestDTO for nulo")
    void mapRequestDtoToOutputDto_shouldThrowNullPointerExceptionForNullInput() {
        // When / Then
        assertThatThrownBy(() -> InventoryApplicationMapper.map((InventoryRequestDTO) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("map(InventoryOutputDTO) deve mapear todos os campos corretamente para InventoryRequestDTO")
    void mapOutputDtoToRequestDto_shouldMapAllFieldsCorrectly() {
        // When
        InventoryRequestDTO result = InventoryApplicationMapper.map(inventoryOutputDTO);

        // Then
        assertThat(result).isNotNull();

        assertThat(result.partId()).isEqualTo(inventoryOutputDTO.partId());
        assertThat(result.quantityOnHand()).isEqualTo(inventoryOutputDTO.quantityOnHand());
    }

    @Test
    @DisplayName("map(InventoryOutputDTO) deve lidar com partId nulo em InventoryOutputDTO")
    void mapOutputDtoToRequestDto_shouldHandleNullIdentifier() {
        // Given
        InventoryOutputDTO outputWithNullId = new InventoryOutputDTO(null, 50);

        // When
        InventoryRequestDTO result = InventoryApplicationMapper.map(outputWithNullId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.partId()).isNull();
        assertThat(result.quantityOnHand()).isEqualTo(50);
    }

    @Test
    @DisplayName("map(InventoryOutputDTO) deve lançar NullPointerException When InventoryOutputDTO for nulo")
    void mapOutputDtoToRequestDto_shouldThrowNullPointerExceptionForNullInput() {
        // When / Then
        assertThatThrownBy(() -> InventoryApplicationMapper.map((InventoryOutputDTO) null))
                .isInstanceOf(NullPointerException.class);
    }
}