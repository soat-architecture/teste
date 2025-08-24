package dev.com.soat.autorepairshop.infrastructure.security;

import dev.com.soat.autorepairshop.infrastructure.api.models.request.AuthRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import dev.com.soat.autorepairshop.infrastructure.security.models.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    public ResponseCookie login(AuthRequestDTO dto) {
        final var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    dto.email(), dto.password()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final var details = (UserDetailsImpl) authentication.getPrincipal();
        return jwtTokenUtils.generateToken(details);
    }

    public ResponseCookie logout() {
        return jwtTokenUtils.cleanToken();
    }
}
