package dev.com.soat.autorepairshop.infrastructure.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static dev.com.soat.autorepairshop.mock.AuthMock.createMockInactiveUserDetails;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockRequestWithCookie;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockRequestWithoutCookies;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockUserDetails;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockUserDetailsWithIdentifier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilsTest {

    @InjectMocks
    private JwtTokenUtils jwtTokenUtils;

    private static final String TEST_SECRET = "6c9c5acb3e09048b863a865d1b524c935b9cfe8c401a1d5396cd63749e02f89427d9543b31d9c0ea1e88089f8412932e2886b273d7bc7920bea814b847dc7ad1";
    private static final Integer TEST_EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenUtils, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenUtils, "expiration", TEST_EXPIRATION);
    }

    @Nested
    @DisplayName("Testes para getTokenFromCookie")
    class GetTokenFromCookieTests {

        @Test
        @DisplayName("Deve extrair token do cookie corretamente")
        void givenRequestWithTokenCookie_whenGetTokenFromCookie_thenReturnsToken() {
            // Given
            final var expectedToken = "valid-jwt-token";
            final var request = createMockRequestWithCookie("token", expectedToken);

            // When
            final var actualToken = jwtTokenUtils.getTokenFromCookie(request);

            // Then
            assertEquals(expectedToken, actualToken);
        }

        @Test
        @DisplayName("Deve retornar null quando não há cookies")
        void givenRequestWithoutCookies_whenGetTokenFromCookie_thenReturnsNull() {
            // Given
            final var request = createMockRequestWithoutCookies();

            // When
            final var token = jwtTokenUtils.getTokenFromCookie(request);

            // Then
            assertNull(token);
        }

        @Test
        @DisplayName("Deve retornar null quando cookie token não existe")
        void givenRequestWithoutTokenCookie_whenGetTokenFromCookie_thenReturnsNull() {
            // Given
            final var request = createMockRequestWithCookie("other-cookie", "value");

            // When
            final var token = jwtTokenUtils.getTokenFromCookie(request);

            // Then
            assertNull(token);
        }

        @Test
        @DisplayName("Deve encontrar cookie token entre múltiplos cookies")
        void givenRequestWithMultipleCookies_whenGetTokenFromCookie_thenReturnsCorrectToken() {
            // Given
            final var expectedToken = "correct-token";
            final var request = mock(HttpServletRequest.class);
            final var cookies = new Cookie[]{
                    new Cookie("session", "session-value"),
                    new Cookie("token", expectedToken),
                    new Cookie("preferences", "pref-value")
            };
            when(request.getCookies()).thenReturn(cookies);

            // When
            final var actualToken = jwtTokenUtils.getTokenFromCookie(request);

            // Then
            assertEquals(expectedToken, actualToken);
        }
    }

    @Nested
    @DisplayName("Testes para generateToken")
    class GenerateTokenTests {

        @Test
        @DisplayName("Deve gerar token válido para usuário ativo")
        void givenActiveUser_whenGenerateToken_thenReturnsValidResponseCookie() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var result = jwtTokenUtils.generateToken(userDetails);

            // Then
            assertNotNull(result);
            assertEquals("token", result.getName());
            assertNotNull(result.getValue());
            assertFalse(result.getValue().isEmpty());
            assertTrue(result.isHttpOnly());
            assertTrue(result.isSecure());
            assertEquals("/", result.getPath());
            assertEquals(216_000, result.getMaxAge().getSeconds());
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException para usuário inativo")
        void givenInactiveUser_whenGenerateToken_thenThrowsAccessDeniedException() {
            // Given
            final var userDetails = createMockInactiveUserDetails();

            // When & Then
            final var exception = assertThrows(AccessDeniedException.class,
                    () -> jwtTokenUtils.generateToken(userDetails));
            assertEquals("auth.exception.is.not.enabled", exception.getMessage());
        }

        @Test
        @DisplayName("Deve gerar token com claims corretos")
        void givenUserDetails_whenGenerateToken_thenTokenContainsCorrectClaims() {
            // Given
            final var userDetails = createMockUserDetails();

            // When
            final var cookie = jwtTokenUtils.generateToken(userDetails);
            final var token = cookie.getValue();

            // Then
            assertTrue(jwtTokenUtils.validateToken(token));
            assertEquals(userDetails.getEmail(), jwtTokenUtils.getUserName(token));

            final var authorities = jwtTokenUtils.getAuthoritiesFromToken(token);
            assertNotNull(authorities);

            final var expirationDate = jwtTokenUtils.getExpirationDate(token);
            assertNotNull(expirationDate);
            assertTrue(expirationDate.after(new Date()));
        }
    }

    @Nested
    @DisplayName("Testes para validateToken")
    class ValidateTokenTests {

        @Test
        @DisplayName("Deve validar token válido corretamente")
        void givenValidToken_whenValidateToken_thenReturnsTrue() {
            // Given
            final var userDetails = createMockUserDetails();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();

            // When
            final var isValid = jwtTokenUtils.validateToken(token);

            // Then
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Deve retornar false para token malformado")
        void givenMalformedToken_whenValidateToken_thenReturnsFalse() {
            // Given
            final var malformedToken = "invalid.token.format";

            // When
            final var isValid = jwtTokenUtils.validateToken(malformedToken);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve retornar false para token vazio")
        void givenEmptyToken_whenValidateToken_thenReturnsFalse() {
            // Given
            final var emptyToken = "";

            // When
            final var isValid = jwtTokenUtils.validateToken(emptyToken);

            // Then
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Deve retornar false para token null")
        void givenNullToken_whenValidateToken_thenReturnsFalse() {
            // When
            final var isValid = jwtTokenUtils.validateToken(null);

            // Then
            assertFalse(isValid);
        }
    }

    @Nested
    @DisplayName("Testes para getUserName")
    class GetUserNameTests {

        @Test
        @DisplayName("Deve extrair username do token corretamente")
        void givenValidToken_whenGetUserName_thenReturnsCorrectUsername() {
            // Given
            final var userDetails = createMockUserDetails();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();

            // When
            final var username = jwtTokenUtils.getUserName(token);

            // Then
            assertEquals("example@example.com", username);
        }

        @Test
        @DisplayName("Deve lançar exceção para token inválido")
        void givenInvalidToken_whenGetUserName_thenThrowsException() {
            // Given
            final var invalidToken = "invalid.token";

            // When & Then
            assertThrows(Exception.class, () -> jwtTokenUtils.getUserName(invalidToken));
        }
    }

    @Nested
    @DisplayName("Testes para getAuthoritiesFromToken")
    class GetAuthoritiesFromTokenTests {

        @Test
        @DisplayName("Deve extrair authorities do token corretamente")
        void givenValidToken_whenGetAuthoritiesFromToken_thenReturnsAuthorities() {
            // Given
            final var userDetails = createMockUserDetails();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();

            // When
            final var authorities = jwtTokenUtils.getAuthoritiesFromToken(token);

            // Then
            assertNotNull(authorities);
        }

        @Test
        @DisplayName("Deve lançar exceção para token inválido")
        void givenInvalidToken_whenGetAuthoritiesFromToken_thenThrowsException() {
            // Given
            final var invalidToken = "invalid.token";

            // When & Then
            assertThrows(Exception.class, () -> jwtTokenUtils.getAuthoritiesFromToken(invalidToken));
        }
    }

    @Nested
    @DisplayName("Testes para getExpirationDate")
    class GetExpirationDateTests {

        @Test
        @DisplayName("Deve extrair data de expiração do token corretamente")
        void givenValidToken_whenGetExpirationDate_thenReturnsCorrectDate() {
            // Given
            final var userDetails = createMockUserDetails();
            final var beforeGeneration = new Date();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();
            final var afterGeneration = new Date();

            // When
            final var expirationDate = jwtTokenUtils.getExpirationDate(token);

            // Then
            assertNotNull(expirationDate);
            assertTrue(expirationDate.after(beforeGeneration));
            // A data de expiração deve ser aproximadamente 1 hora após a geração
            final var expectedExpiration = new Date(afterGeneration.getTime() + TEST_EXPIRATION);
            final var timeDifference = Math.abs(expirationDate.getTime() - expectedExpiration.getTime());
            assertTrue(timeDifference < 5000); // Diferença menor que 5 segundos
        }

        @Test
        @DisplayName("Deve lançar exceção para token inválido")
        void givenInvalidToken_whenGetExpirationDate_thenThrowsException() {
            // Given
            final var invalidToken = "invalid.token";

            // When & Then
            assertThrows(Exception.class, () -> jwtTokenUtils.getExpirationDate(invalidToken));
        }
    }

    @Nested
    @DisplayName("Testes para cleanToken")
    class CleanTokenTests {

        @Test
        @DisplayName("Deve criar cookie de limpeza corretamente")
        void whenCleanToken_thenReturnsInvalidationCookie() {
            // When
            final var result = jwtTokenUtils.cleanToken();

            // Then
            assertNotNull(result);
            assertEquals("token", result.getName());
            assertEquals("", result.getValue());
            assertEquals("/", result.getPath());
            assertEquals(0, result.getMaxAge().getSeconds());
            assertTrue(result.isHttpOnly());
            assertTrue(result.isSecure());
        }

        @Test
        @DisplayName("Deve retornar sempre o mesmo tipo de cookie de limpeza")
        void whenCleanTokenMultipleTimes_thenReturnsConsistentCookies() {
            // When
            final var cookie1 = jwtTokenUtils.cleanToken();
            final var cookie2 = jwtTokenUtils.cleanToken();

            // Then
            assertEquals(cookie1.getName(), cookie2.getName());
            assertEquals(cookie1.getValue(), cookie2.getValue());
            assertEquals(cookie1.getMaxAge(), cookie2.getMaxAge());
            assertEquals(cookie1.getPath(), cookie2.getPath());
            assertEquals(cookie1.isHttpOnly(), cookie2.isHttpOnly());
            assertEquals(cookie1.isSecure(), cookie2.isSecure());
        }
    }

    @Nested
    @DisplayName("Testes de integração e cenários complexos")
    class JwtTokenUtils_IT {

        @Test
        @DisplayName("Deve manter consistência em todo o ciclo de vida do token")
        void whenCompleteTokenLifecycle_thenMaintainsConsistency() {
            // Given
            final var userDetails = createMockUserDetails();

            // When - Gerar token
            final var cookie = jwtTokenUtils.generateToken(userDetails);
            final var token = cookie.getValue();

            // Then - Validar token
            assertTrue(jwtTokenUtils.validateToken(token));

            // And - Extrair informações
            final var extractedUsername = jwtTokenUtils.getUserName(token);
            final var extractedAuthorities = jwtTokenUtils.getAuthoritiesFromToken(token);
            final var extractedExpiration = jwtTokenUtils.getExpirationDate(token);

            // Then - Verificar consistência
            assertEquals(userDetails.getEmail(), extractedUsername);
            assertNotNull(extractedAuthorities);
            assertNotNull(extractedExpiration);
            assertTrue(extractedExpiration.after(new Date()));
        }

        @Test
        @DisplayName("Deve lidar corretamente com diferentes tipos de usuários")
        void givenDifferentUserTypes_whenProcessingTokens_thenHandlesCorrectly() {
            // Test com usuário ativo
            final var activeUser = createMockUserDetails();
            final var activeToken = jwtTokenUtils.generateToken(activeUser);
            assertTrue(jwtTokenUtils.validateToken(activeToken.getValue()));

            // Test com usuário inativo - deve lançar exceção
            final var inactiveUser = createMockInactiveUserDetails();
            assertThrows(AccessDeniedException.class,
                    () -> jwtTokenUtils.generateToken(inactiveUser));
        }

        @Test
        @DisplayName("Deve extrair token do request e validar corretamente")
        void givenRequestWithValidToken_whenExtractAndValidate_thenWorksCorrectly() {
            // Given
            final var userDetails = createMockUserDetails();
            final var generatedToken = jwtTokenUtils.generateToken(userDetails).getValue();
            final var request = createMockRequestWithCookie("token", generatedToken);

            // When
            final var extractedToken = jwtTokenUtils.getTokenFromCookie(request);
            final var isValid = jwtTokenUtils.validateToken(extractedToken);

            // Then
            assertEquals(generatedToken, extractedToken);
            assertTrue(isValid);
        }

        @Test
        @DisplayName("Deve funcionar corretamente com configurações personalizadas")
        void givenCustomConfigurations_whenGeneratingToken_thenUsesCorrectSettings() {
            // Given
            final var customExpiration = 7200000; // 2 hours
            ReflectionTestUtils.setField(jwtTokenUtils, "expiration", customExpiration);
            final var userDetails = createMockUserDetails();

            // When
            final var before = new Date();
            final var cookie = jwtTokenUtils.generateToken(userDetails);
            final var token = cookie.getValue();
            final var expirationDate = jwtTokenUtils.getExpirationDate(token);

            // Then
            final var expectedExpiration = new Date(before.getTime() + customExpiration);
            final var timeDifference = Math.abs(expirationDate.getTime() - expectedExpiration.getTime());
            assertTrue(timeDifference < 5000);
        }
    }

    @Nested
    @DisplayName("Testes para getIdentifierFromToken")
    class GetIdentifierFromTokenTests {

        @Test
        @DisplayName("Deve extrair partId do token corretamente")
        void givenValidTokenWithIdentifier_whenGetIdentifierFromToken_thenReturnsCorrectIdentifier() {
            // Given
            final var userDetails = createMockUserDetails();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();

            // When
            final var identifier = jwtTokenUtils.getIdentifierFromToken(token);

            // Then
            assertNotNull(identifier);
            assertEquals(1L, identifier); // Baseado no mock que retorna partId = 1L
        }

        @Test
        @DisplayName("Deve lançar AccessDeniedException quando token for null")
        void givenNullToken_whenGetIdentifierFromToken_thenThrowsAccessDeniedException() {
            // When & Then
            final var exception = assertThrows(AccessDeniedException.class,
                    () -> jwtTokenUtils.getIdentifierFromToken(null));
            assertEquals("auth.exception.is.not.enabled", exception.getMessage());
        }

        @Test
        @DisplayName("Deve lançar exceção para token inválido")
        void givenInvalidToken_whenGetIdentifierFromToken_thenThrowsException() {
            // Given
            final var invalidToken = "invalid.token.format";

            // When & Then
            assertThrows(Exception.class, () -> jwtTokenUtils.getIdentifierFromToken(invalidToken));
        }

        @Test
        @DisplayName("Deve lançar exceção para token malformado")
        void givenMalformedToken_whenGetIdentifierFromToken_thenThrowsException() {
            // Given
            final var malformedToken = "eyJhbGciOiJIUzI1NiJ9.invalid.signature";

            // When & Then
            assertThrows(Exception.class, () -> jwtTokenUtils.getIdentifierFromToken(malformedToken));
        }

        @Test
        @DisplayName("Deve extrair partId de diferentes usuários corretamente")
        void givenDifferentUsers_whenGetIdentifierFromToken_thenReturnsCorrectIdentifiers() {
            // Given - Primeiro usuário
            final var userDetails1 = createMockUserDetailsWithIdentifier(100L);
            final var token1 = jwtTokenUtils.generateToken(userDetails1).getValue();

            // Given - Segundo usuário
            final var userDetails2 = createMockUserDetailsWithIdentifier(200L);
            final var token2 = jwtTokenUtils.generateToken(userDetails2).getValue();

            // When
            final var identifier1 = jwtTokenUtils.getIdentifierFromToken(token1);
            final var identifier2 = jwtTokenUtils.getIdentifierFromToken(token2);

            // Then
            assertEquals(100L, identifier1);
            assertEquals(200L, identifier2);
        }

        @Test
        @DisplayName("Deve manter consistência do partId durante ciclo completo do token")
        void givenUserWithIdentifier_whenCompleteTokenCycle_thenMaintainsIdentifierConsistency() {
            // Given
            final Long expectedIdentifier = 999L;
            final var userDetails = createMockUserDetailsWithIdentifier(expectedIdentifier);

            // When - Gerar token
            final var cookie = jwtTokenUtils.generateToken(userDetails);
            final var token = cookie.getValue();

            // Then - Validar token
            assertTrue(jwtTokenUtils.validateToken(token));

            // And - Extrair partId
            final var extractedIdentifier = jwtTokenUtils.getIdentifierFromToken(token);

            // Then - Verificar consistência
            assertEquals(expectedIdentifier, extractedIdentifier);
        }

        @Test
        @DisplayName("Deve extrair partId quando token contém múltiplos claims")
        void givenTokenWithMultipleClaims_whenGetIdentifierFromToken_thenReturnsCorrectIdentifier() {
            // Given
            final var userDetails = createMockUserDetails();
            final var token = jwtTokenUtils.generateToken(userDetails).getValue();

            // When
            final var identifier = jwtTokenUtils.getIdentifierFromToken(token);
            final var username = jwtTokenUtils.getUserName(token);
            final var authorities = jwtTokenUtils.getAuthoritiesFromToken(token);

            // Then - Todos os claims devem ser extraídos corretamente
            assertNotNull(identifier);
            assertNotNull(username);
            assertNotNull(authorities);
            assertEquals(1L, identifier);
            assertEquals("example@example.com", username);
        }
    }
}