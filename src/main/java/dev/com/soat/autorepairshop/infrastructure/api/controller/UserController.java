package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.usecase.user.CreateUserUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.DeleteUserUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.FindAllUsersPageableUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.FindUserByEmailUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.UpdateRoleUserUseCase;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.UserMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UserRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.USERS_TAG;

@Tag(name = USERS_TAG, description = "Operações relacionadas aos usuários (funcionários) da plataforma.")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final FindUserByEmailUseCase findUserByEmailUseCase;
    private final FindAllUsersPageableUseCase findAllUsersPageableUseCase;
    private final UpdateRoleUserUseCase updateRoleUserUseCase;
    private final DeleteUserUseCase  deleteUserUseCase;
    private final PasswordEncoder encoder;

    @PostMapping
    @Operation(
            summary = "Criação de um novo funcionário",
            description = "Endpoint responsável pelo cadastro de um novo funcionário no sistema.",
            requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados do funcionário",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Funcionário cadastrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos!"),
                    @ApiResponse(responseCode = "409", description = "Conflito para cadastro do funcionário."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ASSISTANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponseDTO<DefaultDataDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        final var password = encoder.encode(dto.password());
        final var application = UserMapper.map(dto, password);
        createUserUseCase.execute(application);
        return DefaultResponseDTO.created();
    }

    @GetMapping
    @Operation(
            summary = "Busca de funcionário por e-mail",
            description = "Endpoint responsável por buscar os dados do funcionário através do e-mail.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Funcionário encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "E-mail em formato inválido!"),
                    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<UserResponseDTO> findByEmail(final Principal principal) {
        var domain = findUserByEmailUseCase.execute(principal.getName());
        return DefaultResponseDTO.success(UserMapper.map(domain));
    }

    @GetMapping("/all")
    @Operation(
            summary = "Busca paginada de funcionários",
            description = "Endpoint responsável por realizar a busca paginada dos funcionários.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor")
            }
    )
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ASSISTANT')")
    @ResponseStatus(HttpStatus.OK)
    public DefaultResponseDTO<PageInfoGenericUtils<UserResponseDTO>> findAllPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        final var domains = findAllUsersPageableUseCase.execute(page, size, sortBy, direction);

        final var userResponses = domains.content()
                .stream()
                .map(UserMapper::map)
                .toList();

        final var pageResponse = new PageInfoGenericUtils<>(
                userResponses,
                domains.pageNumber(),
                domains.pageSize(),
                domains.totalElements(),
                domains.totalPages()
        );
        return DefaultResponseDTO.success(pageResponse);
    }

    @PatchMapping("/update-role")
    @Operation(
            summary = "Realiza a atuailização de role de funcionário por e-mail",
            description = "Endpoint responsável por realizar a atualização de role do funcionário através do e-mail.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Role atualizada com sucesso."),
                    @ApiResponse(responseCode = "400", description = "E-mail em formato inválido!"),
                    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor")
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void updateRole(
            @RequestHeader final String employeeMail,
            @RequestHeader final String newRole
    ) {
        updateRoleUserUseCase.execute(employeeMail, newRole);
    }

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "Realiza o delete do funcionário pelo identificador.",
            description = "Endpoint responsável por realizar o delete do funcionário através do identificador.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Exclusão realizada com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Funcionário não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro genérico do servidor")
            }
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final Long userId) {
        deleteUserUseCase.execute(userId);
    }
}
