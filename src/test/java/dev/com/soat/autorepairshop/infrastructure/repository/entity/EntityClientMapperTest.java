package dev.com.soat.autorepairshop.infrastructure.repository.entity;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.EntityClientMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityClientMapperTest {

    @Test
    void shouldMapClientDomainToClientEntityCorrectly() {
        Long clientId = 1L;
        LocalDateTime now = LocalDateTime.now();

        ClientDomain domain = new ClientDomain(
            clientId,
            "John Doe",
            "36524781007",
            "(15) 2443-4456",
            "john.doe@example.com",
            ClientStatus.ACTIVE,
            now,
            now
        );

        ClientEntity resultEntity = EntityClientMapper.map(clientId, domain);

        // Assert
        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getId()).isEqualTo(clientId);
        assertThat(resultEntity.getName()).isEqualTo("John Doe");
        assertThat(resultEntity.getDocument()).isEqualTo("36524781007");
        assertThat(resultEntity.getPhone()).isEqualTo("(15) 2443-4456");
        assertThat(resultEntity.getStatus()).isEqualTo(ClientStatus.ACTIVE);
        assertThat(resultEntity.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(resultEntity.getCreatedAt()).isEqualTo(now);
        assertThat(resultEntity.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void shouldMapCorrectlyWhenIdIsNull() {
        Long clientId = null;
        LocalDateTime now = LocalDateTime.now();

        ClientDomain domain = new ClientDomain(
            clientId,
            "Jane Doe",
            "58415620020",
            "(15) 2443-4456",
            "john.doe@example.com",
            ClientStatus.ACTIVE,
            now,
            now
        );

        ClientEntity resultEntity = EntityClientMapper.map(null, domain);

        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getId()).isNull();
        assertThat(resultEntity.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDomainIsNull() {
        Long clientId = 1L;
        var entity = EntityClientMapper.map(clientId, null);
        assertNull(entity);
    }

}