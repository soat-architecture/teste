package dev.com.soat.autorepairshop.infrastructure.security;

import dev.com.soat.autorepairshop.infrastructure.api.models.request.AuthRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static dev.com.soat.autorepairshop.mock.AuthMock.createMockAuthentication;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockLogoutCookie;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockToken;
import static dev.com.soat.autorepairshop.mock.AuthMock.createValidAuthRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Testes para login de usuário")
    class LoginTests {

        @Test
        @DisplayName("Deve realizar login com sucesso e retornar ResponseCookie")
        void givenValidCredentials_whenLogin_thenReturnsResponseCookie() {
            // Given
            final var authRequest = createValidAuthRequest();
            final var mockAuthentication = createMockAuthentication();
            final var mockToken = createMockToken();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenUtils.generateToken(any(UserDetailsImpl.class)))
                    .thenReturn(mockToken);

            // When
            final var result = authService.login(authRequest);

            // Then
            assertNotNull(result);
            assertEquals(mockToken, result);
            assertEquals("token", result.getName());
            assertEquals("mock-jwt-token", result.getValue());
            assertTrue(result.isHttpOnly());
            assertTrue(result.isSecure());
            assertEquals("/", result.getPath());
            assertEquals(216_000, result.getMaxAge().getSeconds());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenUtils).generateToken(any(UserDetailsImpl.class));
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve lançar BadCredentialsException para credenciais inválidas")
        void givenInvalidCredentials_whenLogin_thenThrowsBadCredentialsException() {
            // Given
            final var authRequest = createValidAuthRequest();
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // When & Then
            assertThrows(BadCredentialsException.class, () -> authService.login(authRequest));
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(jwtTokenUtils);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve lançar DisabledException para usuário desabilitado")
        void givenDisabledUser_whenLogin_thenThrowsDisabledException() {
            // Given
            final var authRequest = createValidAuthRequest();
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new DisabledException("User is disabled"));

            // When & Then
            assertThrows(DisabledException.class, () -> authService.login(authRequest));
            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verifyNoInteractions(jwtTokenUtils);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }

        @Test
        @DisplayName("Deve configurar o SecurityContext após login bem-sucedido")
        void givenValidCredentials_whenLogin_thenSetsSecurityContext() {
            // Given
            final var authRequest = createValidAuthRequest();
            final var mockAuthentication = createMockAuthentication();
            final var mockToken = createMockToken();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenUtils.generateToken(any(UserDetailsImpl.class)))
                    .thenReturn(mockToken);

            // When
            authService.login(authRequest);

            // Then
            final var securityContext = SecurityContextHolder.getContext();
            assertNotNull(securityContext.getAuthentication());
            assertEquals(mockAuthentication, securityContext.getAuthentication());
        }

        @Test
        @DisplayName("Deve criar UsernamePasswordAuthenticationToken com parâmetros corretos")
        void givenAuthRequest_whenLogin_thenCreatesCorrectAuthenticationToken() {
            // Given
            final var authRequest = new AuthRequestDTO("test@example.com", "testPassword");
            final var mockAuthentication = createMockAuthentication();
            final var mockToken = createMockToken();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenUtils.generateToken(any(UserDetailsImpl.class)))
                    .thenReturn(mockToken);

            // When
            authService.login(authRequest);

            // Then
            verify(authenticationManager).authenticate(argThat(auth -> {
                final var token = (UsernamePasswordAuthenticationToken) auth;
                return "test@example.com".equals(token.getPrincipal()) &&
                        "testPassword".equals(token.getCredentials());
            }));
        }
    }

    @Nested
    @DisplayName("Testes para logout de usuário")
    class LogoutTests {

        @Test
        @DisplayName("Deve realizar logout com sucesso e retornar cookie de invalidação")
        void whenLogout_thenReturnsInvalidationCookie() {
            // Given
            final var mockLogoutCookie = createMockLogoutCookie();
            when(jwtTokenUtils.cleanToken()).thenReturn(mockLogoutCookie);

            // When
            final var result = authService.logout();

            // Then
            assertNotNull(result);
            assertEquals(mockLogoutCookie, result);
            assertEquals("token", result.getName());
            assertEquals("", result.getValue());
            assertEquals(0, result.getMaxAge().getSeconds());
            assertTrue(result.isHttpOnly());
            assertTrue(result.isSecure());
            assertEquals("/", result.getPath());

            verify(jwtTokenUtils).cleanToken();
        }

        @Test
        @DisplayName("Deve chamar jwtTokenUtils.cleanToken() uma única vez")
        void whenLogout_thenCallsCleanTokenOnce() {
            // Given
            final var mockLogoutCookie = createMockLogoutCookie();
            when(jwtTokenUtils.cleanToken()).thenReturn(mockLogoutCookie);

            // When
            authService.logout();

            // Then
            verify(jwtTokenUtils, times(1)).cleanToken();
        }

        @Test
        @DisplayName("Deve retornar o mesmo cookie retornado pelo JwtTokenUtils")
        void whenLogout_thenReturnsSameCookieFromJwtTokenUtils() {
            // Given
            final var expectedCookie = ResponseCookie.from("token", "")
                    .path("/")
                    .maxAge(0)
                    .secure(true)
                    .httpOnly(true)
                    .build();
            when(jwtTokenUtils.cleanToken()).thenReturn(expectedCookie);

            // When
            final var actualCookie = authService.logout();

            // Then
            assertEquals(expectedCookie, actualCookie);
        }
    }

    @Nested
    @DisplayName("Testes de integração entre métodos")
    class AuthService_IT {

        @Test
        @DisplayName("Deve manter consistência entre login e logout")
        void givenSuccessfulLogin_whenLogout_thenBothOperationsWorkCorrectly() {
            // Given - Setup para login
            final var authRequest = createValidAuthRequest();
            final var mockAuthentication = createMockAuthentication();
            final var mockLoginToken = createMockToken();
            final var mockLogoutCookie = createMockLogoutCookie();

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(mockAuthentication);
            when(jwtTokenUtils.generateToken(any(UserDetailsImpl.class)))
                    .thenReturn(mockLoginToken);
            when(jwtTokenUtils.cleanToken()).thenReturn(mockLogoutCookie);

            // When - Realizar login
            final var loginResult = authService.login(authRequest);

            // Then - Verificar login
            assertNotNull(loginResult);
            assertEquals("mock-jwt-token", loginResult.getValue());
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());

            // When - Realizar logout
            final var logoutResult = authService.logout();

            // Then - Verificar logout
            assertNotNull(logoutResult);
            assertEquals("", logoutResult.getValue());
            assertEquals(0, logoutResult.getMaxAge().getSeconds());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(jwtTokenUtils).generateToken(any(UserDetailsImpl.class));
            verify(jwtTokenUtils).cleanToken();
        }
    }
}