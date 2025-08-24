package dev.com.soat.autorepairshop.application.usecase.user;

import dev.com.soat.autorepairshop.application.mapper.UserApplicationMapper;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserValidationUtils userValidationUtils;

    @InjectMocks
    private CreateUserUseCase createUserUseCase;

    @Test
    @DisplayName("Deve criar usuário com sucesso quando usuário não existir")
    void shouldCreateUserSuccessfully() {
        // Given
        final var userDto = UserMock.buildMockInputDTO();
        var userDomain = UserApplicationMapper.toDomain(userDto);

        doNothing().when(userValidationUtils).validateNewUserByEmail(userDto.email());
        when(userGateway.save(any(UserDomain.class))).thenReturn(userDomain);

        // When/Then
        assertDoesNotThrow(() -> createUserUseCase.execute(userDto));

        verify(userValidationUtils).validateNewUserByEmail(userDto.email());
        verify(userGateway).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o documento for inválido")
    void shouldThrowDomainExceptionWhenDocumentIsInvalid() {
        // Given
        final var invalidDocument = "12345678901";
        final var userDto = UserMock.buildMockInputDTO(
                null,
                invalidDocument,
                null,
                null,
                null,
                null
        );

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
        verify(userGateway, times(0)).save(any(UserDomain.class));
    }


    @ParameterizedTest
    @DisplayName("Deve lançar DomainException para CPFs com dígitos repetidos")
    @ValueSource(strings = {
            "11111111111",
            "22222222222",
            "33333333333",
            "44444444444",
            "55555555555"
    })
    void shouldThrowDomainExceptionForCPFsWithSameDigits(final String document) {
        // Given
        final var userDto = UserMock.buildMockInputDTO(
                null,
                document,
                null,
                null,
                null,
                null
        );

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("document.is.not.valid", exception.getMessage());
        verify(userGateway, times(0)).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar GenericException quando ocorrer um erro inesperado")
    void shouldThrowGenericExceptionWhenUnexpectedError() {
        // Given
        final var userDto = UserMock.buildMockInputDTO();

        doThrow(new RuntimeException("Unexpected error")).when(userValidationUtils).validateNewUserByEmail(userDto.email());

        // When/Then
        GenericException exception = assertThrows(
                GenericException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("generic.error", exception.getMessage());
        verify(userValidationUtils).validateNewUserByEmail(userDto.email());
        verify(userGateway, never()).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o email for inválido")
    void shouldThrowDomainExceptionWhenEmailIsInvalid() {
        // Given
        final var invalidEmail = "invalid-email";
        final var userDto = UserMock.buildMockInputDTO(
                null,
                null,
                invalidEmail,
                null,
                null,
                null
        );
        doNothing().when(userValidationUtils).validateNewUserByEmail(userDto.email());

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("email.is.not.valid", exception.getMessage());
        verify(userValidationUtils, times(1)).validateNewUserByEmail(userDto.email());
        verify(userGateway, times(0)).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando a senha estiver vazia")
    void shouldThrowDomainExceptionWhenPasswordIsEmpty() {
        // Given
        final var userDto = UserMock.buildMockInputDTO(
                null,
                null,
                null,
                "",
                null,
                null
        );

        doNothing().when(userValidationUtils).validateNewUserByEmail(userDto.email());
        when(userGateway.save(any(UserDomain.class))).thenThrow(new DomainException("password.is.required"));

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("password.is.required", exception.getMessage());
        verify(userValidationUtils).validateNewUserByEmail(userDto.email());
        verify(userGateway).save(any(UserDomain.class));
    }

    @Test
    @DisplayName("Deve lançar DomainException quando o perfil for inválido")
    void shouldThrowDomainExceptionWhenRoleIsInvalid() {
        // Given
        final var invalidRole = "INVALID_ROLE";
        final var userDto = UserMock.buildMockInputDTO(
                null,
                null,
                null,
                null,
                null,
                invalidRole
        );

        doNothing().when(userValidationUtils).validateNewUserByEmail(userDto.email());

        // When/Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createUserUseCase.execute(userDto)
        );

        assertEquals("generic.error", exception.getMessage());
        verify(userValidationUtils, times(1)).validateNewUserByEmail(userDto.email());
        verify(userGateway, times(0)).save(any(UserDomain.class));
    }
}