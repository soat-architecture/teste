package dev.com.soat.autorepairshop.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.*;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Sistema de mecânica.",
                version = "1.0",
                description = "Documentação dos endpoints da API."
        ),
        tags = {
                @Tag(name = AUTH_TAG, description = "Operações de Autenticação (Login/Logout)"),
                @Tag(name = USERS_TAG, description = "Operações relacionadas aos usuários (funcionários) da plataforma."),
                @Tag(name = PARTS_TAG, description = "Operações relacionadas às peças e insumos."),
                @Tag(name = SERVICES_TAG, description = "Operações relacionadas aos serviços prestados pela oficina."),
                @Tag(name = INVENTORY_TAG, description = "Operações relacionadas ao estoque de peças e insumos."),
                @Tag(name = CLIENTS_TAG, description = "Operações relacionadas aos clientes."),
                @Tag(name = VEHICLES_TAG, description = "Operações relacionadas aos veículos da plataforma."),
                @Tag(name = SERVICE_ORDERS_TAG, description = "Operações relacionadas às ordens de serviço."),
                @Tag(name = BUDGETS_TAG, description = "Operações relacionadas aos orçamentos."),
        }
)
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = SECURITY_SCHEMA_NAME,
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = SECURITY_TOKEN_NAME
)
@io.swagger.v3.oas.annotations.security.SecurityRequirement(name = SECURITY_SCHEMA_NAME)
public class OpenApiConfig {

}
