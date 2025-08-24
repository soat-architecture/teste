package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.com.soat.autorepairshop.application.models.input.UserInputDTO;
import dev.com.soat.autorepairshop.application.models.output.UserOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.user.CreateUserUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.DeleteUserUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.FindAllUsersPageableUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.FindUserByEmailUseCase;
import dev.com.soat.autorepairshop.application.usecase.user.UpdateRoleUserUseCase;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.adapter.LocalDateTimeAdapter;
import dev.com.soat.autorepairshop.mock.UserMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private FindUserByEmailUseCase findUserByEmailUseCase;

    @Mock
    private FindAllUsersPageableUseCase findAllUsersPageableUseCase;

    @Mock
    private UpdateRoleUserUseCase updateRoleUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
    }

    private Principal createMockPrincipal(String name) {
        return () -> name;
    }

    @Nested
    @DisplayName("Testes para criação de usuário (POST /v1/users)")
    class CreateUserTests {
        @Test
        @DisplayName("Deve criar um usuário com sucesso e retornar 201 Created")
        void givenValidUserRequest_whenCreateUser_thenReturns201() throws Exception {
            // Given
            final var dto = UserMock.buildMockRequestDTO();
            when(encoder.encode(dto.password())).thenReturn("encoded-password");
            doNothing().when(createUserUseCase).execute(any(UserInputDTO.class));

            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.message").value("Successfully in the request. No given response."));
        }

        @Test
        @DisplayName("Deve retornar 409 Conflict quando o usuário já existe")
        void givenExistingUser_whenCreateUser_thenReturns409() throws Exception {
            // Given
            final var dto = UserMock.buildMockRequestDTO();
            when(encoder.encode(dto.password())).thenReturn("encoded-password");
            doThrow(new ConflictException("user.already.exists")).when(createUserUseCase).execute(any(UserInputDTO.class));

            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(dto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.data.error").value("Conflict Error"));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para dados inválidos (ex: e-mail nulo)")
        void givenInvalidUserRequest_whenCreateUser_thenReturns400() throws Exception {
            // Given
            final var dto = UserMock.buildMockRequestDTO(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            // When & Then
            mockMvc.perform(post("/v1/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(gson.toJson(dto)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes para buscar usuário por e-mail (GET /v1/users)")
    class FindUserByEmailTests {
        @Test
        @DisplayName("Deve encontrar um usuário com sucesso e retornar 200 OK")
        void givenExistingEmail_whenFindByEmail_thenReturns200AndUserData() throws Exception {
            // Given
            String email = "found@example.com";
            final var dto = UserMock.buildMockOutputDTO();
            when(findUserByEmailUseCase.execute(email)).thenReturn(dto);

            // When & Then
            mockMvc.perform(get("/v1/users")
                            .principal(createMockPrincipal(email)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.identifier").value(dto.identifier()))
                    .andExpect(jsonPath("$.data.name").value(dto.name()))
                    .andExpect(jsonPath("$.data.email").value(dto.email()));
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando o usuário não for encontrado")
        void givenNonExistingEmail_whenFindByEmail_thenReturns404() throws Exception {
            // Given
            String email = "notfound@example.com";
            when(findUserByEmailUseCase.execute(email)).thenThrow(new NotFoundException("user.not.found"));

            // When & Then
            mockMvc.perform(get("/v1/users")
                            .principal(createMockPrincipal(email)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.data.error").value("Not Found Error"));
        }

        @Test
        @DisplayName("Deve retornar 400 Bad Request para um formato de e-mail inválido")
        void givenInvalidEmailFormat_whenFindByEmail_thenReturns400() throws Exception {
            // Given
            String invalidEmail = "invalid-email";
            when(findUserByEmailUseCase.execute(invalidEmail)).thenThrow(new DomainException("invalid.email.format"));

            // When & Then
            mockMvc.perform(get("/v1/users")
                            .principal(createMockPrincipal(invalidEmail)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.data.error").value("Business Error"));
        }
    }

    @Nested
    @DisplayName("Testes para buscar todos os usuários paginados (GET /v1/users/all)")
    class FindAllUsersPaginatedTests {
        @Test
        @DisplayName("Deve retornar uma página de usuários com sucesso e status 200 OK")
        void givenUsersExist_whenFindAllPaginated_thenReturns200AndPageOfUsers() throws Exception {
            // Given
            final var dto = UserMock.buildMockOutputDTO();
            List<UserOutputDTO> userList = List.of(dto);
            var pageInfo = new PageInfoGenericUtils<>(userList, 0, 10, 1L, 1);

            when(findAllUsersPageableUseCase.execute(0, 10, "name", "asc")).thenReturn(pageInfo);

            // When & Then
            mockMvc.perform(get("/v1/users/all")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "name")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.content[0].name").value(dto.name()))
                    .andExpect(jsonPath("$.data.totalElements").value(1));
        }

        @Test
        @DisplayName("Deve retornar uma página vazia com sucesso e status 200 OK")
        void givenNoUsersExist_whenFindAllPaginated_thenReturns200AndEmptyPage() throws Exception {
            // Given
            var emptyPage = new PageInfoGenericUtils<UserOutputDTO>(Collections.emptyList(), 0, 10, 0L, 0);

            when(findAllUsersPageableUseCase.execute(0, 10, "name", "asc")).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/v1/users/all")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "name")
                            .param("direction", "asc"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("Testes para atualizar a role do usuário (PATCH /v1/users/update-role)")
    class UpdateRoleTests {
        @Test
        @DisplayName("Deve atualizar a role com sucesso e retornar 204 No Content")
        void givenValidData_whenUpdateRole_thenReturns204() throws Exception {
            // Given
            String email = "user@example.com";
            String newRole = "ADMIN";
            doNothing().when(updateRoleUserUseCase).execute(email, newRole);

            // When & Then
            mockMvc.perform(patch("/v1/users/update-role")
                            .header("employeeMail", email)
                            .header("newRole", newRole))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando o usuário para atualizar não é encontrado")
        void givenNonExistingUser_whenUpdateRole_thenReturns404() throws Exception {
            // Given
            String email = "notfound@example.com";
            String newRole = "ADMIN";
            doThrow(new NotFoundException("user.not.found")).when(updateRoleUserUseCase).execute(email, newRole);

            // When & Then
            mockMvc.perform(patch("/v1/users/update-role")
                            .header("employeeMail", email)
                            .header("newRole", newRole))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.data.error").value("Not Found Error"));
        }
    }

    @Nested
    @DisplayName("Testes para deletar um usuário (DELETE /v1/users/{userId})")
    class DeleteUserTests {
        @Test
        @DisplayName("Deve deletar um usuário com sucesso e retornar 204 No Content")
        @WithMockUser
            // Usuário autenticado básico
        void givenExistingUserId_whenDelete_thenReturns204() throws Exception {
            // Given
            Long userId = 1L;
            doNothing().when(deleteUserUseCase).execute(userId);

            // When & Then
            mockMvc.perform(delete("/v1/users/{userId}", userId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 404 Not Found quando o usuário a ser deletado não existe")
        @WithMockUser
        void givenNonExistingUserId_whenDelete_thenReturns404() throws Exception {
            // Given
            final var userId = 99L;
            doThrow(new NotFoundException("user.not.found")).when(deleteUserUseCase).execute(userId);

            // When & Then
            mockMvc.perform(delete("/v1/users/{userId}", userId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.data.error").value("Not Found Error"));
        }
    }
}