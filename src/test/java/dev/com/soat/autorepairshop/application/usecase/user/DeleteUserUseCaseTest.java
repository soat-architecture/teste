package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserValidationUtils userValidationUtils;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    @DisplayName("Deve deletar usuário com sucesso quando informado um ID válido")
    void shouldDeleteUserSuccessfully() {
        // Given
        final var userId = 1L;
        final var user = mock(UserDomain.class);
        when(userValidationUtils.validateUserExistenceById(userId)).thenReturn(user);

        // When
        deleteUserUseCase.execute(userId);

        // Then
        verify(user).delete();
        verify(userGateway).save(user);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando ocorrer erro na operação de deleção")
    void shouldThrowDomainExceptionWhenDeleteOperationFails() {
        // Given
        final var userId = 1L;
        final var user = mock(UserDomain.class);
        when(userValidationUtils.validateUserExistenceById(userId)).thenReturn(user);
        doThrow(new DomainException("error.deleting.user")).when(user).delete();

        // When/Then
        assertThrows(DomainException.class, () -> deleteUserUseCase.execute(userId));
        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer um erro inesperado")
    void shouldThrowGenericExceptionWhenUnexpectedErrorOccurs() {
        // Given
        final var userId = 1L;
        final var user = mock(UserDomain.class);
        when(userValidationUtils.validateUserExistenceById(userId)).thenReturn(user);
        when(userGateway.save(user)).thenThrow(new RuntimeException("Unexpected error"));

        // When/Then
        assertThrows(GenericException.class, () -> deleteUserUseCase.execute(userId));
    }
}