package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(

        @Schema(description = "Email do funcionário", example = "admin@admin.com")
        @NotBlank(message = "O e-mail é obrigatório.")
        String email,

        @Schema(description = "Senha do funcionário", example = "dg93Q2WtK1!#")
        @NotBlank(message = "A senha é obrigatória.")
        String password
) {
}
