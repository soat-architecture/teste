package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.PartDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.PartEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityPartMapperTest {

    @Test
    @DisplayName("Should map PartDomain to PartEntity correctly")
    void shouldMapPartDomainToPartEntityCorrectly() {
        Long partId = 1L;
        LocalDateTime now = LocalDateTime.now();

        PartDomain domain = PartDomain.restore(
                partId,
                "Pastilha de Freio",
                "SKU-123",
                "Pastilha de freio de alta performance",
                "Bosch",
                new BigDecimal("200.00"),
                new BigDecimal("150.00"),
                true,
                now,
                now
        );

        PartEntity resultEntity = PartEntityMapper.toEntity(domain);

        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getPartId()).isEqualTo(partId);
        assertThat(resultEntity.getName()).isEqualTo("Pastilha de Freio");
        assertThat(resultEntity.getSku()).isEqualTo("SKU-123");
        assertThat(resultEntity.getDescription()).isEqualTo("Pastilha de freio de alta performance");
        assertThat(resultEntity.getBrand()).isEqualTo("Bosch");
        assertThat(resultEntity.getSellingPrice()).isEqualByComparingTo("200.00");
        assertThat(resultEntity.getBuyPrice()).isEqualByComparingTo("150.00");
        assertThat(resultEntity.getActive()).isTrue();
        assertThat(resultEntity.getCreatedAt()).isEqualTo(now);
        assertThat(resultEntity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map PartEntity to PartDomain correctly")
    void shouldMapPartEntityToPartDomainCorrectly() {
        Long partId = 1L;
        LocalDateTime now = LocalDateTime.now();

        PartEntity entity = new PartEntity(
                partId,
                "Pastilha de Freio",
                "SKU-123",
                "Pastilha de freio de alta performance",
                "Bosch",
                new BigDecimal("200.00"),
                new BigDecimal("150.00"),
                true,
                now,
                now
        );

        PartDomain resultDomain = PartEntityMapper.toDomain(entity);

        assertThat(resultDomain).isNotNull();
        assertThat(resultDomain.getIdentifier()).isEqualTo(partId);
        assertThat(resultDomain.getName()).isEqualTo("Pastilha de Freio");
        assertThat(resultDomain.getSku()).isEqualTo("SKU-123");
        assertThat(resultDomain.getDescription()).isEqualTo("Pastilha de freio de alta performance");
        assertThat(resultDomain.getBrand()).isEqualTo("Bosch");
        assertThat(resultDomain.getSellingPrice()).isEqualByComparingTo("200.00");
        assertThat(resultDomain.getBuyPrice()).isEqualByComparingTo("150.00");
        assertThat(resultDomain.getActive()).isTrue();
        assertThat(resultDomain.getCreatedAt()).isEqualTo(now);
        assertThat(resultDomain.getUpdatedAt()).isEqualTo(now);
    }
}

