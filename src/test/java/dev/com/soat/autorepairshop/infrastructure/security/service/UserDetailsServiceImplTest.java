package dev.com.soat.autorepairshop.infrastructure.security.service;

import dev.com.soat.autorepairshop.application.usecase.user.FindUserByEmailUseCase;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static dev.com.soat.autorepairshop.mock.UserMock.buildMockOutputDTO;
import static dev.com.soat.autorepairshop.mock.UserMock.buildMockOutputInactiveDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("Testes para loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Deve carregar usuário ativo com sucesso")
        void givenValidEmail_whenLoadUserByUsername_thenReturnsUserDetails() {
            // Given
            final var email = "john@email.com";
            final var userOutput = buildMockOutputDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(userOutput);

            // When
            final var result = userDetailsService.loadUserByUsername(email);

            // Then
            assertNotNull(result);
            assertInstanceOf(UserDetailsImpl.class, result);
            assertEquals(email, result.getUsername());
            assertTrue(result.isEnabled());
            assertTrue(result.isAccountNonExpired());
            assertTrue(result.isAccountNonLocked());
            assertTrue(result.isCredentialsNonExpired());
            assertNotNull(result.getAuthorities());

            verify(findUserByEmailUseCase).execute(email);
        }

        @Test
        @DisplayName("Deve carregar usuário inativo mas retornar isEnabled = false")
        void givenInactiveUser_whenLoadUserByUsername_thenReturnsDisabledUserDetails() {
            // Given
            final var email = "john@email.com";
            final var userOutput = buildMockOutputInactiveDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(userOutput);

            // When
            final var result = userDetailsService.loadUserByUsername(email);

            // Then
            assertNotNull(result);
            assertInstanceOf(UserDetailsImpl.class, result);
            assertEquals(email, result.getUsername());
            assertFalse(result.isEnabled()); // Usuário inativo
            assertTrue(result.isAccountNonExpired());
            assertTrue(result.isAccountNonLocked());
            assertTrue(result.isCredentialsNonExpired());

            verify(findUserByEmailUseCase).execute(email);
        }

        @Test
        @DisplayName("Deve lançar UsernameNotFoundException quando usuário não for encontrado")
        void givenNonExistentEmail_whenLoadUserByUsername_thenThrowsUsernameNotFoundException() {
            // Given
            final var email = "nonexistent@email.com";
            when(findUserByEmailUseCase.execute(email))
                    .thenThrow(new NotFoundException("john.not.found"));

            // When & Then
            final var exception = assertThrows(NotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(email));

            assertEquals("john.not.found", exception.getMessage());
            verify(findUserByEmailUseCase).execute(email);
        }

        @Test
        @DisplayName("Deve construir UserDetailsImpl corretamente usando UserDetailsImpl.build()")
        void givenUserOutput_whenLoadUserByUsername_thenBuildsUserDetailsCorrectly() {
            // Given
            final var email = "john@email.com";
            final var userOutput = buildMockOutputDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(userOutput);

            // When
            final var result = userDetailsService.loadUserByUsername(email);

            // Then
            assertInstanceOf(UserDetailsImpl.class, result);
            final var userDetailsImpl = (UserDetailsImpl) result;
            assertEquals(userOutput.email(), userDetailsImpl.getEmail());
            assertEquals(userOutput.password(), userDetailsImpl.getPassword());
            assertEquals(userOutput.status(), userDetailsImpl.getStatus());
            assertNotNull(userDetailsImpl.getAuthorities());
        }

        @Test
        @DisplayName("Deve propagar exceções do use case")
        void givenUseCaseThrowsException_whenLoadUserByUsername_thenPropagatesException() {
            // Given
            final var email = "john@email.com";
            final var expectedException = new RuntimeException("Database error");
            when(findUserByEmailUseCase.execute(email)).thenThrow(expectedException);

            // When & Then
            final var actualException = assertThrows(RuntimeException.class,
                    () -> userDetailsService.loadUserByUsername(email));

            assertEquals("Database error", actualException.getMessage());
            verify(findUserByEmailUseCase).execute(email);
        }
        @Test
        @DisplayName("Deve lidar com email null ou vazio")
        void givenNullOrEmptyEmail_whenLoadUserByUsername_thenHandlesGracefully() {
            // Given
            when(findUserByEmailUseCase.execute(null))
                    .thenThrow(new IllegalArgumentException("Email cannot be null"));

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> userDetailsService.loadUserByUsername(null));

            // Given
            final var emptyEmail = "";
            when(findUserByEmailUseCase.execute(emptyEmail))
                    .thenThrow(new IllegalArgumentException("Email cannot be empty"));

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> userDetailsService.loadUserByUsername(emptyEmail));
        }
    }

    @Nested
    @DisplayName("Testes de integração com UserDetailsImpl")
    class UserDetailsServiceImpl_IT {

        @Test
        @DisplayName("Deve garantir que UserDetailsImpl.build() seja chamado internamente")
        void whenLoadUserByUsername_thenUsesUserDetailsImplBuild() {
            // Given
            final var email = "john@email.com";
            final var userOutput = buildMockOutputDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(userOutput);

            // When
            final var result = userDetailsService.loadUserByUsername(email);

            // Then
            // Verificamos se o resultado tem as mesmas propriedades que seriam
            // construídas pelo método UserDetailsImpl.build()
            assertInstanceOf(UserDetailsImpl.class, result);
            final var userDetails = (UserDetailsImpl) result;

            assertEquals(userOutput.email(), userDetails.getUsername());
            assertEquals(userOutput.password(), userDetails.getPassword());
            assertEquals(userOutput.status(), userDetails.getStatus());

            // Verifica se as authorities foram configuradas corretamente
            assertNotNull(userDetails.getAuthorities());
            assertFalse(userDetails.getAuthorities().isEmpty());
        }

        @Test
        @DisplayName("Deve manter consistência entre UserOutputDTO e UserDetailsImpl")
        void givenUserOutputDTO_whenConverted_thenMaintainsDataConsistency() {
            // Given
            final var email = "consistency@email.com";
            final var userOutput = buildMockOutputDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(userOutput);

            // When
            final var userDetails = userDetailsService.loadUserByUsername(email);

            // Then
            final var userDetailsImpl = (UserDetailsImpl) userDetails;
            assertEquals(userOutput.email(), userDetailsImpl.getEmail());
            assertEquals(userOutput.password(), userDetailsImpl.getPassword());
            assertEquals(userOutput.status(), userDetailsImpl.getStatus());
            assertEquals(userOutput.email(), userDetailsImpl.getUsername());

            // Verifica comportamentos específicos do UserDetails
            assertTrue(userDetailsImpl.isAccountNonExpired());
            assertTrue(userDetailsImpl.isAccountNonLocked());
            assertTrue(userDetailsImpl.isCredentialsNonExpired());
            assertTrue(userDetailsImpl.isEnabled());
        }
    }
}