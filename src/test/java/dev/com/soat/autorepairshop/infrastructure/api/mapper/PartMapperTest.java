package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.PartInputDTO;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.PartRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.PartResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PartMapperTest {

    @Test
    @DisplayName("Should map PartRequestDTO to PartInputDTO correctly")
    void mapFromPartRequestDTO_whenValid_shouldReturnPartInputDTO() {
        var requestDto = new PartRequestDTO(
                "Pastilha de Freio",
                "SKU-123",
                "Pastilha para freio dianteiro",
                "Brembo",
                new BigDecimal("150.00"),
                new BigDecimal("100.00")
        );

        PartInputDTO result = PartMapper.map(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Pastilha de Freio");
        assertThat(result.sku()).isEqualTo("SKU-123");
        assertThat(result.description()).isEqualTo("Pastilha para freio dianteiro");
        assertThat(result.brand()).isEqualTo("Brembo");
        assertThat(result.sellingPrice()).isEqualByComparingTo("150.00");
        assertThat(result.buyPrice()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("Should map PartOutputDTO to PartResponseDTO correctly")
    void mapFromPartOutputDTO_whenValid_shouldReturnPartResponseDTO() {
        var now = LocalDateTime.now();

        var outputDto = new PartOutputDTO(
                1L,
                "Óleo de motor",
                "SKU-456",
                "Óleo sintético 5W30",
                "Mobil",
                new BigDecimal("80.00"),
                new BigDecimal("50.00"),
                true,
                now.minusDays(2),
                now
        );

        PartResponseDTO result = PartMapper.map(outputDto);

        assertThat(result).isNotNull();
        assertThat(result.identifier()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Óleo de motor");
        assertThat(result.sku()).isEqualTo("SKU-456");
        assertThat(result.description()).isEqualTo("Óleo sintético 5W30");
        assertThat(result.brand()).isEqualTo("Mobil");
        assertThat(result.sellingPrice()).isEqualByComparingTo("80.00");
        assertThat(result.buyPrice()).isEqualByComparingTo("50.00");
        assertThat(result.active()).isTrue();
        assertThat(result.createdAt()).isEqualTo(now.minusDays(2));
        assertThat(result.updatedAt()).isEqualTo(now);
    }
}
