package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import dev.com.soat.autorepairshop.domain.annotation.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Representa dados de cadastro para um novo funcionário.")
public record UserRequestDTO(

        @Schema(description = "Documento do funcionário.", example = "012.234.567-89")
        @NotBlank(message = "attribute.is.not.blank")
        String document,
        @Schema(description = "Nome do funcionário.", example = "John Doe")
        @NotBlank(message = "attribute.is.not.blank")
        String name,
        @Schema(description = "E-mail do funcionário.", example = "john.doe@example.com")
        @NotBlank(message = "attribute.is.not.blank")
        String email,
        @Schema(description = "Senha do funcionário.", example = "Password!123@")
        @NotBlank(message = "attribute.is.not.blank")
        @Password
        String password,
        @Schema(description = "Senha do funcionário.", example = "2025-07-13T17:00")
        @NotNull(message = "attribute.is.not.null")
        LocalDateTime contractedAt,
        @Schema(description = "Permissão do funcionário.", example = "ADMIN")
        @NotBlank(message = "attribute.is.not.blank")
        String role
) {
}
