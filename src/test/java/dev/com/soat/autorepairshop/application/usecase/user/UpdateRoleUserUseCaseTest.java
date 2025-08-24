package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRoleUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserValidationUtils userValidationUtils;

    @InjectMocks
    private UpdateRoleUserUseCase updateRoleUserUseCase;

    @Test
    @DisplayName("Deve atualizar role do usuário com sucesso")
    void shouldUpdateUserRoleSuccessfully() {
        // Given
        final String email = "usuario@teste.com";
        final String newRole = "ADMIN";
        final var user = UserMock.buildMockDomain();

        when(userValidationUtils.validateUserExistenceByEmail(email)).thenReturn(user);
        when(userGateway.save(any(UserDomain.class))).thenReturn(user);

        // When/Then
        assertDoesNotThrow(() -> updateRoleUserUseCase.execute(email, newRole));

        verify(userValidationUtils).validateUserExistenceByEmail(email);
        verify(userGateway).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email for inválido")
    void shouldThrowDomainExceptionWhenEmailIsInvalid() {
        // Given
        final String invalidEmail = "email-invalido";
        final String newRole = "ADMIN";

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> updateRoleUserUseCase.execute(invalidEmail, newRole)
        );

        assertEquals("email.is.not.valid", exception.getMessage());
        verify(userGateway, never()).findByEmail(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando role for inválida")
    void shouldThrowDomainExceptionWhenRoleIsInvalid() {
        // Given
        final String email = "usuario@teste.com";
        final String invalidRole = "ROLE_INVALID";
        final var user = UserMock.buildMockDomain();

        when(userValidationUtils.validateUserExistenceByEmail(email)).thenReturn(user);

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> updateRoleUserUseCase.execute(email, invalidRole)
        );

        assertEquals("generic.error", exception.getMessage());
        verify(userValidationUtils, times(1)).validateUserExistenceByEmail(email);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer erro inesperado")
    void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
        // Given
        final String email = "usuario@teste.com";
        final String newRole = "ADMIN";

        when(userValidationUtils.validateUserExistenceByEmail(email)).thenThrow(new RuntimeException("Erro inesperado"));

        // When/Then
        GenericException exception = assertThrows(
                GenericException.class,
                () -> updateRoleUserUseCase.execute(email, newRole)
        );

        assertEquals("generic.error", exception.getMessage());
        verify(userValidationUtils).validateUserExistenceByEmail(email);
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email for nulo")
    void shouldThrowDomainExceptionWhenEmailIsNull() {
        // Given
        final String newRole = "ADMIN";

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> updateRoleUserUseCase.execute(null, newRole)
        );

        assertEquals("email.cannot.be.null", exception.getMessage());
        verify(userGateway, never()).findByEmail(any());
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando role for nula")
    void shouldThrowDomainExceptionWhenRoleIsNull() {
        // Given
        final String email = "usuario@teste.com";

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> updateRoleUserUseCase.execute(email, null)
        );

        assertEquals("new.role.cannot.be.null", exception.getMessage());
        verify(userGateway, never()).findByEmail(email);
        verify(userGateway, never()).save(any());
    }
}