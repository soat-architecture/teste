package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserByEmailUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserValidationUtils userValidationUtils;

    @InjectMocks
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @Test
    @DisplayName("Deve retornar usuário quando email existir")
    void shouldReturnUserWhenEmailExists() {
        // Given
        final String email = UserMock.VALID_EMAIL;
        final var userDomain = UserMock.buildMockDomain();

        when(userValidationUtils.validateUserExistenceByEmail(email)).thenReturn(userDomain);

        // When
        final var result = findUserByEmailUseCase.execute(email);

        // Then
        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals(UserMock.VALID_NAME, result.name());
        assertEquals(UserMock.VALID_DOCUMENT, result.document());
        verify(userValidationUtils).validateUserExistenceByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email for inválido")
    void shouldThrowDomainExceptionWhenEmailIsInvalid() {
        // Given
        final String invalidEmail = "email-invalido";

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> findUserByEmailUseCase.execute(invalidEmail)
        );

        assertEquals("email.is.not.valid", exception.getMessage());
        verify(userGateway, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer erro inesperado")
    void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
        // Given
        final String email = "usuario@teste.com";
        when(userValidationUtils.validateUserExistenceByEmail(email)).thenThrow(new RuntimeException("Erro inesperado"));

        // When/Then
        GenericException exception = assertThrows(
                GenericException.class,
                () -> findUserByEmailUseCase.execute(email)
        );

        assertEquals("generic.error", exception.getMessage());
        verify(userValidationUtils).validateUserExistenceByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email for nulo")
    void shouldThrowDomainExceptionWhenEmailIsNull() {
        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> findUserByEmailUseCase.execute(null)
        );

        assertEquals("email.cannot.be.null", exception.getMessage());
        verify(userGateway, never()).findByEmail(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email estiver vazio")
    void shouldThrowDomainExceptionWhenEmailIsEmpty() {
        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> findUserByEmailUseCase.execute("")
        );

        assertEquals("email.cannot.be.null", exception.getMessage());
        verify(userGateway, never()).findByEmail(any());
    }
}
