package dev.com.soat.autorepairshop.infrastructure.api.models.request;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.objects.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Representa os dados de um cliente.")
public record ClientRequestDTO(

    @Schema(description = "Nome do cliente", example = "John Doe")
    @NotBlank(message = "O nome é obrigatório.")
    String name,

    @Schema(description = "Documento do cliente (CPF/CNPJ)", example = "012.234.567-89")
    @NotBlank(message = "O documento é obrigatório.")
    String document,

    @Schema(description = "Telefone do cliente", example = "(01) 99999-9999")
    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = Phone.DEFAULT_PHONE_REGEX, message = "Telefone inválido. Formato esperado: (XX) XXXXX-XXXX ou (XX) XXXX-XXXX")
    String phone,

    @Schema(description = "Email do cliente", example = "john.doe@example.com")
    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email inválido.")
    String email,

    @Schema(description = "Status do cliente", example = "ACTIVE")
    ClientStatus status
) {
}
