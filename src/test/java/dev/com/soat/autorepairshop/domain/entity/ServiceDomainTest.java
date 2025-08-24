package dev.com.soat.autorepairshop.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceDomainTest {

    private static final String SAMPLE_NAME = "Troca de óleo";
    private static final String SAMPLE_DESCRIPTION = "Troca de óleo veicular.";
    private static final BigDecimal SAMPLE_BASE_PRICE = new BigDecimal("1500.00");
    private static final Long SAMPLE_IDENTIFIER = 1L;
    private static final LocalDateTime SAMPLE_CREATED_AT = LocalDateTime.of(2023, 1, 15, 10, 0, 0);
    private static final LocalDateTime SAMPLE_UPDATED_AT = LocalDateTime.of(2023, 1, 15, 11, 30, 0);

    @Test
    @DisplayName("Deve criar uma instância de ServiceDomain com todos os argumentos fornecidos")
    void shouldCreateServiceDomainWithAllArguments() {
        // Cria uma instância de ServiceDomain usando o construtor completo
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );

        assertEquals(SAMPLE_IDENTIFIER, service.getIdentifier(), "O identificador deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_NAME, service.getName(), "O nome deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_DESCRIPTION, service.getDescription(), "A descrição deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_BASE_PRICE, service.getBasePrice(), "O preço base deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_CREATED_AT, service.getCreatedAt(), "A data de criação deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_UPDATED_AT, service.getUpdatedAt(), "A data de atualização deve ser a mesma que a fornecida.");
    }

    @Test
    @DisplayName("Deve criar uma nova instância de ServiceDomain usando o método de fábrica create")
    void shouldCreateNewServiceDomainUsingFactoryMethod() {
        ServiceDomain service = ServiceDomain.create(
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE
        );

        assertNull(service.getIdentifier(), "O identificador deve ser nulo para um novo serviço.");
        assertEquals(SAMPLE_NAME, service.getName(), "O nome deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_DESCRIPTION, service.getDescription(), "A descrição deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_BASE_PRICE, service.getBasePrice(), "O preço base deve ser o mesmo que o fornecido.");
        assertNull(service.getCreatedAt(), "A data de criação deve ser nula para um novo serviço.");
        assertNull(service.getUpdatedAt(), "A data de atualização deve ser nula para um novo serviço.");
    }

    @Test
    @DisplayName("Deve restaurar uma instância de ServiceDomain usando o método de fábrica restore")
    void shouldRestoreServiceDomainUsingFactoryMethod() {
        ServiceDomain service = ServiceDomain.restore(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );

        assertEquals(SAMPLE_IDENTIFIER, service.getIdentifier(), "O identificador deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_NAME, service.getName(), "O nome deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_DESCRIPTION, service.getDescription(), "A descrição deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_BASE_PRICE, service.getBasePrice(), "O preço base deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_CREATED_AT, service.getCreatedAt(), "A data de criação deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_UPDATED_AT, service.getUpdatedAt(), "A data de atualização deve ser a mesma que a fornecida.");
    }

    @Test
    @DisplayName("Deve retornar o identificador correto")
    void shouldReturnCorrectIdentifier() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_IDENTIFIER, service.getIdentifier(), "getIdentifier deve retornar o identificador correto.");
    }

    @Test
    @DisplayName("Deve retornar o nome correto")
    void shouldReturnCorrectName() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_NAME, service.getName(), "getName deve retornar o nome correto.");
    }

    @Test
    @DisplayName("Deve retornar a descrição correta")
    void shouldReturnCorrectDescription() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_DESCRIPTION, service.getDescription(), "getDescription deve retornar a descrição correta.");
    }

    @Test
    @DisplayName("Deve retornar o preço base correto")
    void shouldReturnCorrectBasePrice() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_BASE_PRICE, service.getBasePrice(), "getBasePrice deve retornar o preço base correto.");
    }

    @Test
    @DisplayName("Deve retornar a data de criação correta")
    void shouldReturnCorrectCreatedAt() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_CREATED_AT, service.getCreatedAt(), "getCreatedAt deve retornar a data de criação correta.");
    }

    @Test
    @DisplayName("Deve retornar a data de atualização correta")
    void shouldReturnCorrectUpdatedAt() {
        ServiceDomain service = new ServiceDomain(
                SAMPLE_IDENTIFIER,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                SAMPLE_CREATED_AT,
                SAMPLE_UPDATED_AT
        );
        assertEquals(SAMPLE_UPDATED_AT, service.getUpdatedAt(), "getUpdatedAt deve retornar a data de atualização correta.");
    }

    @Test
    @DisplayName("Deve lidar com valores nulos para identificador, createdAt e updatedAt no construtor")
    void shouldHandleNullValuesInConstructor() {
        ServiceDomain service = new ServiceDomain(
                null,
                SAMPLE_NAME,
                SAMPLE_DESCRIPTION,
                SAMPLE_BASE_PRICE,
                null,
                null
        );

        assertNull(service.getIdentifier(), "O identificador deve ser nulo.");
        assertEquals(SAMPLE_NAME, service.getName(), "O nome deve ser o mesmo que o fornecido.");
        assertEquals(SAMPLE_DESCRIPTION, service.getDescription(), "A descrição deve ser a mesma que a fornecida.");
        assertEquals(SAMPLE_BASE_PRICE, service.getBasePrice(), "O preço base deve ser o mesmo que o fornecido.");
        assertNull(service.getCreatedAt(), "A data de criação deve ser nula.");
        assertNull(service.getUpdatedAt(), "A data de atualização deve ser nula.");
    }
}
