package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.infrastructure.api.models.request.AuthRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AuthMock {
    private AuthMock() {

    }

    private static final Long VALID_IDENTIFIER = 1L;
    private static final String VALID_EMAIL = "example@example.com";
    private static final String VALID_PASSWORD = "Password!123#";
    private static final String VALID_PASSWORD_ENCODED = "$2a$10$v1684brh0bkF9OW0BuLGkOfl8ECKCRAABdu5QZNrt8BtQKr0tm0xK";
    private static final String VALID_STATUS = "ACTIVE";
    private static final String VALID_INACTIVE_STATUS = "INACTIVE";
    private static final String VALID_ADMIN_ROLE = "ROLE_ADMIN";

    public static UserDetailsImpl createMockUserDetails() {
        return new UserDetailsImpl(
                VALID_IDENTIFIER,
                VALID_EMAIL,
                VALID_PASSWORD_ENCODED,
                VALID_STATUS,
                List.of(new SimpleGrantedAuthority(VALID_ADMIN_ROLE))
        );
    }
    public static UserDetailsImpl createMockUserDetailsWithIdentifier(final Long identifier) {
        return new UserDetailsImpl(
                identifier,
                VALID_EMAIL,
                VALID_PASSWORD_ENCODED,
                VALID_STATUS,
                List.of(new SimpleGrantedAuthority(VALID_ADMIN_ROLE))
        );
    }

    public static UserDetailsImpl createMockInactiveUserDetails() {
        return new UserDetailsImpl(
                VALID_IDENTIFIER,
                VALID_EMAIL,
                VALID_PASSWORD_ENCODED,
                VALID_INACTIVE_STATUS,
                List.of(new SimpleGrantedAuthority(VALID_ADMIN_ROLE))
        );
    }

    public static Authentication createMockAuthentication() {
        final var userDetails = createMockUserDetails();
        final var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        return authentication;
    }

    public static AuthRequestDTO createValidAuthRequest() {
        return new AuthRequestDTO(VALID_EMAIL, VALID_PASSWORD);
    }

    public static ResponseCookie createMockToken() {
        return ResponseCookie.from("token", "mock-jwt-token")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(216_000)
                .build();
    }

    public static ResponseCookie createMockLogoutCookie() {
        return ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
    }

    public static HttpServletRequest createMockRequestWithCookie(String cookieName, String cookieValue) {
        final var request = mock(HttpServletRequest.class);
        final var cookie = new Cookie(cookieName, cookieValue);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
        return request;
    }

    public static HttpServletRequest createMockRequestWithoutCookies() {
        final var request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);
        return request;
    }
}
