package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.infrastructure.api.models.request.AuthRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.security.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.AUTH_TAG;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.ok;


@Tag(name = AUTH_TAG, description = "Operações de Autenticação (Login/Logout).")
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Realiza o login do funcionário na plataforma.",
            description = """
                    Endpoint responsável por validação as credenciais do respectivo funcionário. E, em caso de sucesso na validação,
                    realizar a autenticação do funcionário gerando um JWT Token para o mesmo utilizar nas demais requisições.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados de login",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado - Credenciais inválidas"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DefaultResponseDTO<DefaultDataDTO>> login(@Valid @RequestBody AuthRequestDTO dto) {
        final var token = authService.login(dto);
        return ok().header(SET_COOKIE, token.toString()).body(DefaultResponseDTO.ok());
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Realiza o logout do funcionário na plataforma.",
            description = """
                    Endpoint responsável por realizar o logout da plataforma.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido", content = @Content(mediaType = "application/json"))
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<DefaultResponseDTO<DefaultDataDTO>> logout() {
        final var token = authService.logout();
        return ok().header(SET_COOKIE, token.toString()).body(DefaultResponseDTO.ok());
    }
}
