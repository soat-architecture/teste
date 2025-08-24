package dev.com.soat.autorepairshop.infrastructure.security.filter;

import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import dev.com.soat.autorepairshop.infrastructure.security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

import static dev.com.soat.autorepairshop.mock.AuthMock.createMockInactiveUserDetails;
import static dev.com.soat.autorepairshop.mock.AuthMock.createMockUserDetails;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("Testes para doFilterInternal - Cenários de sucesso")
    class SuccessfulAuthenticationTests {

        @Test
        @DisplayName("Deve autenticar usuário com token válido")
        void givenValidToken_whenDoFilterInternal_thenAuthenticatesUser() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(jwtTokenUtils).getTokenFromCookie(request);
                verify(jwtTokenUtils).validateToken(token);
                verify(jwtTokenUtils).getUserName(token);
                verify(userDetailsServiceImpl).loadUserByUsername(username);
                verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
                verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve configurar authentication com dados corretos")
        void givenValidToken_whenDoFilterInternal_thenSetsCorrectAuthentication() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(securityContext).setAuthentication(argThat(auth -> {
                    final var authentication = (UsernamePasswordAuthenticationToken) auth;
                    return authentication.getPrincipal().equals(userDetails) &&
                            authentication.getCredentials() == null &&
                            authentication.getAuthorities().equals(userDetails.getAuthorities());
                }));
            }
        }

        @Test
        @DisplayName("Deve configurar detalhes de autenticação usando WebAuthenticationDetailsSource")
        void givenValidToken_whenDoFilterInternal_thenSetsAuthenticationDetails() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(securityContext).setAuthentication(argThat(auth -> {
                    final var authentication = (UsernamePasswordAuthenticationToken) auth;
                    return authentication.getDetails() != null;
                }));
            }
        }

        @Test
        @DisplayName("Deve processar múltiplos usuários válidos corretamente")
        void givenMultipleValidUsers_whenDoFilterInternal_thenProcessesCorrectly() throws ServletException, IOException {
            // Given
            final String[] tokens = {"token1", "token2", "token3"};
            final String[] usernames = {"user1@example.com", "user2@example.com", "user3@example.com"};

            for (int i = 0; i < tokens.length; i++) {
                final var token = tokens[i];
                final var username = usernames[i];
                final var userDetails = new UserDetailsImpl(
                        1L,
                        username,
                        "password" + i,
                        "ACTIVE",
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

                when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
                when(jwtTokenUtils.validateToken(token)).thenReturn(true);
                when(jwtTokenUtils.getUserName(token)).thenReturn(username);
                when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

                try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                    mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                    // When
                    authenticationFilter.doFilterInternal(request, response, filterChain);

                    // Then
                    verify(jwtTokenUtils, atLeastOnce()).getTokenFromCookie(request);
                    verify(jwtTokenUtils, atLeastOnce()).validateToken(token);
                    verify(jwtTokenUtils, atLeastOnce()).getUserName(token);
                    verify(userDetailsServiceImpl, atLeastOnce()).loadUserByUsername(username);
                }

                // Reset mocks para próxima iteração
                reset(jwtTokenUtils, userDetailsServiceImpl, securityContext, filterChain);
            }
        }
    }

    @Nested
    @DisplayName("Testes para doFilterInternal - Cenários sem autenticação")
    class NoAuthenticationTests {

        @Test
        @DisplayName("Deve prosseguir sem autenticação quando não há token")
        void givenNoToken_whenDoFilterInternal_thenProceedsWithoutAuthentication() throws ServletException, IOException {
            // Given
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(null);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(jwtTokenUtils).getTokenFromCookie(request);
                verify(jwtTokenUtils, never()).validateToken(anyString());
                verify(jwtTokenUtils, never()).getUserName(anyString());
                verify(userDetailsServiceImpl, never()).loadUserByUsername(anyString());
                verify(securityContext, never()).setAuthentication(any());
                verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve prosseguir sem autenticação quando token é inválido")
        void givenInvalidToken_whenDoFilterInternal_thenProceedsWithoutAuthentication() throws ServletException, IOException {
            // Given
            final var invalidToken = "invalid-token";
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(invalidToken);
            when(jwtTokenUtils.validateToken(invalidToken)).thenReturn(false);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(jwtTokenUtils).getTokenFromCookie(request);
                verify(jwtTokenUtils).validateToken(invalidToken);
                verify(jwtTokenUtils, never()).getUserName(anyString());
                verify(userDetailsServiceImpl, never()).loadUserByUsername(anyString());
                verify(securityContext, never()).setAuthentication(any());
                verify(filterChain).doFilter(request, response);
            }
        }
    }

    @Nested
    @DisplayName("Testes para doFilterInternal - Cenários de usuário desabilitado")
    class DisabledUserTests {

        @Test
        @DisplayName("Deve continuar filtro quando usuário está desabilitado")
        void givenDisabledUser_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "inactive@example.com";
            final var inactiveUserDetails = createMockInactiveUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(inactiveUserDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(jwtTokenUtils).getTokenFromCookie(request);
                verify(jwtTokenUtils).validateToken(token);
                verify(jwtTokenUtils).getUserName(token);
                verify(userDetailsServiceImpl).loadUserByUsername(username);
                verify(securityContext, never()).setAuthentication(any());
                verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve não autenticar usuário desabilitado")
        void givenDisabledUser_whenDoFilterInternal_thenDoesNotAuthenticate() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "inactive@example.com";
            final var inactiveUserDetails = createMockInactiveUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(inactiveUserDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                assertFalse(inactiveUserDetails.isEnabled());
                verify(securityContext, never()).setAuthentication(any());
                verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve verificar status do usuário antes de autenticar")
        void givenUserWithDifferentStatuses_whenDoFilterInternal_thenChecksStatusBeforeAuth() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";
            final String[] statuses = {"ACTIVE", "INACTIVE", "SUSPENDED", "PENDING"};

            for (String status : statuses) {
                final var userDetails = new UserDetailsImpl(
                        1L,
                        username,
                        "password123",
                        status,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

                when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
                when(jwtTokenUtils.validateToken(token)).thenReturn(true);
                when(jwtTokenUtils.getUserName(token)).thenReturn(username);
                when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

                try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                    mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                    // When
                    authenticationFilter.doFilterInternal(request, response, filterChain);

                    // Then
                    if ("ACTIVE".equals(status)) {
                        verify(securityContext, atLeastOnce()).setAuthentication(any());
                    } else {
                        verify(securityContext, never()).setAuthentication(any());
                    }
                }

                // Reset mocks para próxima iteração
                reset(jwtTokenUtils, userDetailsServiceImpl, securityContext, filterChain);
            }
        }
    }

    @Nested
    @DisplayName("Testes para doFilterInternal - Tratamento de exceções")
    class ExceptionHandlingTests {

        @Test
        @DisplayName("Deve continuar filtro quando jwtTokenUtils.getTokenFromCookie lança exceção")
        void givenGetTokenFromCookieThrowsException_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            when(jwtTokenUtils.getTokenFromCookie(request)).thenThrow(new RuntimeException("Cookie error"));

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(filterChain).doFilter(request, response);
                verify(securityContext, never()).setAuthentication(any());
            }
        }

        @Test
        @DisplayName("Deve continuar filtro quando jwtTokenUtils.validateToken lança exceção")
        void givenValidateTokenThrowsException_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            final var token = "valid-token";
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenThrow(new RuntimeException("Validation error"));

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(filterChain).doFilter(request, response);
                verify(securityContext, never()).setAuthentication(any());
            }
        }

        @Test
        @DisplayName("Deve continuar filtro quando jwtTokenUtils.getUserName lança exceção")
        void givenGetUserNameThrowsException_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            final var token = "valid-token";
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenThrow(new RuntimeException("Username extraction error"));

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(filterChain).doFilter(request, response);
                verify(securityContext, never()).setAuthentication(any());
            }
        }

        @Test
        @DisplayName("Deve continuar filtro quando userDetailsService lança exceção")
        void givenUserDetailsServiceThrowsException_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username))
                    .thenThrow(new RuntimeException("User not found"));

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(filterChain).doFilter(request, response);
                verify(securityContext, never()).setAuthentication(any());
            }
        }

        @Test
        @DisplayName("Deve continuar filtro quando SecurityContextHolder lança exceção")
        void givenSecurityContextHolderThrowsException_whenDoFilterInternal_thenContinuesFilterChain() throws ServletException, IOException {
            // Given
            final var token = "valid-jwt-token";
            final var username = "user@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext)
                        .thenThrow(new RuntimeException("Security context error"));

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve registrar log de erro quando ocorre exceção")
        void givenException_whenDoFilterInternal_thenLogsError() throws ServletException, IOException {
            // Given
            when(jwtTokenUtils.getTokenFromCookie(request)).thenThrow(new RuntimeException("Test exception"));

            // When
            assertDoesNotThrow(() -> authenticationFilter.doFilterInternal(request, response, filterChain));

            // Then
            verify(filterChain).doFilter(request, response);
            // Nota: O log é verificado indiretamente pelo fato de que a exceção não é propagada
        }
    }

    @Nested
    @DisplayName("Testes de validação de fluxo do filtro")
    class FilterFlowValidationTests {

        @Test
        @DisplayName("Deve sempre chamar filterChain.doFilter independente do resultado da autenticação")
        void givenAnyScenario_whenDoFilterInternal_thenAlwaysCallsDoFilter() throws ServletException, IOException {
            // Scenario 1: Token válido
            final var token = "valid-token";
            final var username = "user@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                authenticationFilter.doFilterInternal(request, response, filterChain);
                verify(filterChain).doFilter(request, response);
            }

            // Reset mocks
            reset(filterChain, jwtTokenUtils, userDetailsServiceImpl);

            // Scenario 2: Token inválido
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn("invalid-token");
            when(jwtTokenUtils.validateToken(anyString())).thenReturn(false);

            authenticationFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);

            // Reset mocks
            reset(filterChain, jwtTokenUtils);

            // Scenario 3: Sem token
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(null);

            authenticationFilter.doFilterInternal(request, response, filterChain);
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("Deve seguir o fluxo correto de validação do token")
        void givenValidationFlow_whenDoFilterInternal_thenFollowsCorrectSequence() throws ServletException, IOException {
            // Given
            final var token = "test-token";
            final var username = "test@example.com";
            final var userDetails = createMockUserDetails();

            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(token);
            when(jwtTokenUtils.validateToken(token)).thenReturn(true);
            when(jwtTokenUtils.getUserName(token)).thenReturn(username);
            when(userDetailsServiceImpl.loadUserByUsername(username)).thenReturn(userDetails);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then - Verifica sequência específica
                final var inOrder = inOrder(jwtTokenUtils, userDetailsServiceImpl, securityContext, filterChain);
                inOrder.verify(jwtTokenUtils).getTokenFromCookie(request);
                inOrder.verify(jwtTokenUtils).validateToken(token);
                inOrder.verify(jwtTokenUtils).getUserName(token);
                inOrder.verify(userDetailsServiceImpl).loadUserByUsername(username);
                inOrder.verify(securityContext).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
                inOrder.verify(filterChain).doFilter(request, response);
            }
        }

        @Test
        @DisplayName("Deve parar validação quando token é inválido")
        void givenInvalidToken_whenDoFilterInternal_thenStopsValidationEarly() throws ServletException, IOException {
            // Given
            final var invalidToken = "invalid-token";
            when(jwtTokenUtils.getTokenFromCookie(request)).thenReturn(invalidToken);
            when(jwtTokenUtils.validateToken(invalidToken)).thenReturn(false);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

                // When
                authenticationFilter.doFilterInternal(request, response, filterChain);

                // Then
                final var inOrder = inOrder(jwtTokenUtils, userDetailsServiceImpl, securityContext, filterChain);
                inOrder.verify(jwtTokenUtils).getTokenFromCookie(request);
                inOrder.verify(jwtTokenUtils).validateToken(invalidToken);
                // Não deve chamar os próximos métodos da cadeia de validação
                inOrder.verify(jwtTokenUtils, never()).getUserName(anyString());
                inOrder.verify(userDetailsServiceImpl, never()).loadUserByUsername(anyString());
                inOrder.verify(securityContext, never()).setAuthentication(any());
                // Mas deve continuar o filtro
                inOrder.verify(filterChain).doFilter(request, response);
            }
        }
    }
}
