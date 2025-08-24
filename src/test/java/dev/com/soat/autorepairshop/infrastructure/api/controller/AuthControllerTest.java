package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.adapter.LocalDateTimeAdapter;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.AuthRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.security.AuthService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
    }

    private AuthRequestDTO createValidAuthRequest() {
        return new AuthRequestDTO("user@example.com", "password123");
    }

    private ResponseCookie createMockToken() {
        return ResponseCookie.from("auth-token", "mock-jwt-token")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600)
                .build();
    }

    private ResponseCookie createMockLogoutToken() {
        return ResponseCookie.from("auth-token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    @Nested
    @DisplayName("Testes para login de usuário (POST /v1/auth/login)")
    class LoginTests {

        @Test
        @DisplayName("Deve realizar login com sucesso e retornar 200 OK com cookie")
        void givenValidCredentials_whenLogin_thenReturns200WithCookie() throws Exception {
            // Given
            final var authRequest = createValidAuthRequest();
            final var mockToken = createMockToken();
            when(authService.login(any(AuthRequestDTO.class))).thenReturn(mockToken);

            // When & Then
            mockMvc.perform(post("/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(authRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.message").value("Successfully in the request. No given response."))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE, Matchers.containsString("auth-token=mock-jwt-token")));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando o usuário não existe")
        void givenNonExistentUser_whenLogin_thenReturns404() throws Exception {
            // Given
            final var authRequest = createValidAuthRequest();
            when(authService.login(any(AuthRequestDTO.class)))
                    .thenThrow(new NotFoundException("user.not.found"));

            // When & Then
            mockMvc.perform(post("/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(authRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.data.error").value("Not Found Error"));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para credenciais inválidas")
        void givenInvalidCredentials_whenLogin_thenReturns400() throws Exception {
            // Given
            final var authRequest = createValidAuthRequest();
            when(authService.login(any(AuthRequestDTO.class)))
                    .thenThrow(new DomainException("invalid.credentials"));

            // When & Then
            mockMvc.perform(post("/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(authRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.data.error").value("Business Error"));
        }
    }

    @Nested
    @DisplayName("Testes para logout de usuário (POST /v1/auth/logout)")
    class LogoutTests {

        @Test
        @DisplayName("Deve realizar logout com sucesso e retornar 200 OK com cookie de invalidação")
        void whenLogout_thenReturns200WithInvalidationCookie() throws Exception {
            // Given
            final var mockLogoutToken = createMockLogoutToken();
            when(authService.logout()).thenReturn(mockLogoutToken);

            // When & Then
            mockMvc.perform(post("/v1/auth/logout"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.message").value("Successfully in the request. No given response."))
                    .andExpect(header().string(HttpHeaders.SET_COOKIE,
                            org.hamcrest.Matchers.containsString("auth-token=; Path=/; Max-Age=0")));        }
    }
}
