package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.ServiceEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ServiceEntityMapperTest {

    @Test
    @DisplayName("Deve mapear para entidade corretamente")
    void shouldMapToEntityCorrectly() {

        ServiceDomain domain = new ServiceDomain(
                1L,
                "Troca de óleo",
                "Troca de óleo veicular",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ServiceEntity entity = ServiceEntityMapper.toEntity(domain);

        assertNotNull(domain);
        assertEquals(domain.getIdentifier(), entity.getId());
        assertEquals(domain.getName(), entity.getName());
        assertEquals(domain.getDescription(), entity.getDescription());
        assertEquals(domain.getBasePrice(), entity.getBasePrice());
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
        assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt());

    }

    @Test
    @DisplayName("Deve mapear para entidade corretamente")
    void shouldMapToUpdatedEntityCorrectly() {

        ServiceDomain domain = new ServiceDomain(
                1L,
                "Troca de óleo",
                "Troca de óleo veicular",
                new BigDecimal("100.00"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ServiceEntity entity = ServiceEntityMapper.toEntity(2L, domain);

        assertNotNull(domain);
        assertNotEquals(domain.getIdentifier(), entity.getId());
        assertEquals(domain.getName(), entity.getName());
        assertEquals(domain.getDescription(), entity.getDescription());
        assertEquals(domain.getBasePrice(), entity.getBasePrice());
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
        assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt());

    }

    @Test
    @DisplayName("Deve mapear para entidade corretamente")
    void shouldMapToDomainCorrectly() {

        ServiceEntity entity = new ServiceEntity(
                1L,
                "Troca de óleo",
                "Troca de óleo veicular",
                new BigDecimal("100.00")
        );

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        ServiceDomain domain = ServiceEntityMapper.toDomain(entity);

        assertNotNull(entity);
        assertEquals(entity.getId(), domain.getIdentifier());
        assertEquals(entity.getName(), domain.getName());
        assertEquals(entity.getDescription(), domain.getDescription());
        assertEquals(entity.getBasePrice(), domain.getBasePrice());
        assertEquals(entity.getCreatedAt(), domain.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), domain.getUpdatedAt());

    }
}
