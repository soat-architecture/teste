package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.ClientEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    @Test
    @DisplayName("Should map ClientInputDTO to ClientDomain correctly")
    void mapFromClientInputDTO_whenValid_shouldReturnClientDomain() {
        var inputDto = new ClientInputDTO(
            1L,
            "John Doe",
            "112.353.320-20",
            "(47) 9555-1234",
            "john.doe@example.com",
            ClientStatus.ACTIVE
        );

        ClientDomain result = ClientMapper.map(inputDto);

        assertThat(result).isNotNull();
        assertThat(result.getIdentifier()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getFormatedDocument()).isEqualTo("112.353.320-20");
        assertThat(result.getPhone()).isEqualTo("(47) 9555-1234");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getStatus()).isEqualTo(ClientStatus.ACTIVE);
        assertThat(result.getCreatedAt()).isNull();
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when ClientInputDTO is null")
    void mapFromClientInputDTO_whenNull_shouldReturnNull() {
        ClientInputDTO inputDto = null;

        ClientDomain result = ClientMapper.map(inputDto);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should map ClientEntity to ClientDomain correctly")
    void mapFromClientEntity_whenValid_shouldReturnClientDomain() {
        var entity = new ClientEntity();
        entity.setId(1L);
        entity.setName("Jane Smith");
        entity.setDocument("142.605.280-41");
        entity.setPhone("(47) 9555-1234");
        entity.setEmail("jane.smith@example.com");
        entity.setStatus(ClientStatus.INACTIVE);
        entity.setCreatedAt(LocalDateTime.parse("2023-10-27T10:00:00"));

        ClientDomain result = ClientMapper.map(entity);

        assertThat(result).isNotNull();
        assertThat(result.getIdentifier()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Jane Smith");
        assertThat(result.getFormatedDocument()).isEqualTo("142.605.280-41");
        assertThat(result.getPhone()).isEqualTo("(47) 9555-1234");
        assertThat(result.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(result.getStatus()).isEqualTo(ClientStatus.INACTIVE);
        assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.parse("2023-10-27T10:00:00"));
        assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("Should return null when ClientEntity is null")
    void mapFromClientEntity_whenNull_shouldReturnNull() {
        ClientEntity entity = null;

        ClientDomain result = ClientMapper.map(entity);

        assertThat(result).isNull();
    }
}