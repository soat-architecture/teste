package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.UserDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidationUtilsTest {

    @Spy
    @InjectMocks
    private UserValidationUtils userValidationUtils;

    @Mock
    private UserGateway userGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existir, buscando pelo id")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistById(){
        // Given
        final Long userID = 102L;

        when(userGateway.findByUserId(userID)).thenReturn(null);

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userValidationUtils.validateUserExistenceById(userID)
        );

        assertEquals("user.not.found", exception.getMessage());
        verify(userGateway).findByUserId(userID);
    }

    @Test
    @DisplayName("Deve retornar o usuário quando ele existir, buscando pelo id")
    void shouldReturnUserDomainWhenUserExistById() {
        // Given
        final Long userId = 102L;
        var existingUser = Mockito.mock(UserDomain.class);

        when(userGateway.findByUserId(userId)).thenReturn(existingUser);

        UserDomain result = userValidationUtils.validateUserExistenceById(userId);

        assertEquals(UserDomain.class, result.getClass());
        verify(userGateway).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existir, buscando pelo email")
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistByEmail() {
        // Given
        final String email = "inexistente@teste.com";

        when(userGateway.findByEmail(email)).thenReturn(null);

        // When/Then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userValidationUtils.validateUserExistenceByEmail(email)
        );

        assertEquals("user.not.found", exception.getMessage());
        verify(userGateway).findByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar o usuário quando ele existir, buscando pelo email")
    void shouldReturnUserDomainWhenUserExistByEmail() {
        // Given
        final String email = "inexistente@teste.com";
        var existingUser = Mockito.mock(UserDomain.class);

        when(userGateway.findByEmail(email)).thenReturn(existingUser);

        UserDomain result = userValidationUtils.validateUserExistenceByEmail(email);

        assertEquals(UserDomain.class, result.getClass());
        verify(userGateway).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando usuário já existir")
    void shouldThrowDomainExceptionWhenUserExistsByEmail() {
        // Given
        String email = "test@test.com";
        var existingUser = Mockito.mock(UserDomain.class);

        when(userGateway.findByEmail(email)).thenReturn(existingUser);

        // When/Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userValidationUtils.validateNewUserByEmail(email)
        );

        assertEquals("user.already.exists", exception.getMessage());
        verify(userGateway).findByEmail(email);
    }

    @Test
    @DisplayName("Não deve acontecer nada quando usuário não existir")
    void shouldDoNothingWhenUserDoesNotExistsByEmail() {
        String email = "test@test.com";

        when(userGateway.findByEmail(email)).thenReturn(null);

        userValidationUtils.validateNewUserByEmail(email);

        verify(userGateway).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando usuário já existir")
    void shouldThrowDomainExceptionWhenUserExistsByDocument() {
        // Given
        String document = "45997418000153";
        var existingUser = Mockito.mock(UserDomain.class);

        when(userGateway.findByDocument(document)).thenReturn(existingUser);

        // When/Then
        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userValidationUtils.validateNewUserByDocument(document)
        );

        assertEquals("user.already.exists", exception.getMessage());
        verify(userGateway).findByDocument(document);
    }

    @Test
    @DisplayName("Não deve acontecer nada quando usuário não existir")
    void shouldDoNothingWhenUserDoesNotExistsByDocument() {
        String document = "45997418000153";

        when(userGateway.findByDocument(document)).thenReturn(null);

        userValidationUtils.validateNewUserByDocument(document);

        verify(userGateway).findByDocument(document);
    }

}