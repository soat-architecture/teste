
package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.com.soat.autorepairshop.application.models.input.BudgetInputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetDetailOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetItemOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.BudgetOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.budget.ApproveBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.CreateBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.FindBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.NotApproveBudgetUseCase;
import dev.com.soat.autorepairshop.application.usecase.budget.UpdateBudgetUseCase;
import dev.com.soat.autorepairshop.domain.enums.BudgetStatus;
import dev.com.soat.autorepairshop.domain.enums.ItemTypeEnum;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.BudgetMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetItemRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.BudgetRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetDetailResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetItemResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.BudgetResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.security.jwt.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BudgetControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private CreateBudgetUseCase createUseCase;

    @Mock
    private UpdateBudgetUseCase updateBudgetUseCase;

    @Mock
    private FindBudgetUseCase findBudgetUseCase;

    @Mock
    private ApproveBudgetUseCase approveBudgetUseCase;

    @Mock
    private NotApproveBudgetUseCase notApproveBudgetUseCase;

    @InjectMocks
    private BudgetController budgetController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private BudgetRequestDTO budgetRequestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(budgetController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();

        budgetRequestDTO = new BudgetRequestDTO(
                1L,
                "Notas de teste",
                List.of(new BudgetItemRequestDTO(1L, ItemTypeEnum.PART, 1.0))
        );
    }

    @Nested
    @DisplayName("Testes de Criação de Orçamento")
    class CreateBudgetTests {

        @Test
        @DisplayName("Deve criar um orçamento e retornar status 201 (Created)")
        void createBudget_shouldReturnCreated_whenBudgetIsValid() throws Exception {
            // given
            String token = "valid-token";
            Long employeeId = 1L;
            BudgetInputDTO inputDTO = createBudgetInputDTO();

            when(jwtTokenUtils.getTokenFromCookie(any())).thenReturn(token);
            when(jwtTokenUtils.getIdentifierFromToken(token)).thenReturn(employeeId);

            try (MockedStatic<BudgetMapper> mockedStatic = mockStatic(BudgetMapper.class)) {
                mockedStatic.when(() -> BudgetMapper.mapToNewBudget(any(), eq(employeeId)))
                        .thenReturn(inputDTO);

                doNothing().when(createUseCase).execute(inputDTO);

                // when/then
                mockMvc.perform(post("/v1/budgets")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(budgetRequestDTO)))
                        .andExpect(status().isCreated());

                verify(createUseCase).execute(inputDTO);
            }
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Orçamento")
    class UpdateBudgetTests {

        @Test
        @DisplayName("Deve atualizar um orçamento e retornar status 200 (OK)")
        void updateBudget_shouldReturnOk_whenBudgetIsValid() throws Exception {
            // given
            Long budgetId = 1L;
            String token = "valid-token";
            Long employeeId = 1L;
            BudgetInputDTO inputDTO = createBudgetInputDTO();

            when(jwtTokenUtils.getTokenFromCookie(any())).thenReturn(token);
            when(jwtTokenUtils.getIdentifierFromToken(token)).thenReturn(employeeId);

            try (MockedStatic<BudgetMapper> mockedStatic = mockStatic(BudgetMapper.class)) {
                mockedStatic.when(() -> BudgetMapper.map(any(), eq(employeeId), eq(budgetId)))
                        .thenReturn(inputDTO);

                doNothing().when(updateBudgetUseCase).execute(inputDTO);

                // when/then
                mockMvc.perform(put("/v1/budgets/{budgetId}", budgetId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(budgetRequestDTO)))
                        .andExpect(status().isOk());

                verify(updateBudgetUseCase).execute(inputDTO);
            }
        }
    }

    @Nested
    @DisplayName("Testes de Aprovação de Orçamento")
    class ApproveBudgetTests {

        @Test
        @DisplayName("Deve aprovar um orçamento e retornar status 200 (OK)")
        void approveBudget_shouldReturnOk_whenBudgetExists() throws Exception {
            // given
            Long serviceOrderId = 1L;
            doNothing().when(approveBudgetUseCase).execute(serviceOrderId);

            // when/then
            mockMvc.perform(get("/v1/budgets/approve/{serviceOrderId}", serviceOrderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));

            verify(approveBudgetUseCase).execute(serviceOrderId);
        }

        @Test
        @DisplayName("Deve não aprovar um orçamento e retornar status 200 (OK)")
        void notApproveBudget_shouldReturnOk_whenBudgetExists() throws Exception {
            // given
            Long serviceOrderId = 1L;
            doNothing().when(notApproveBudgetUseCase).execute(serviceOrderId);

            // when/then
            mockMvc.perform(get("/v1/budgets/not-approve/{serviceOrderId}", serviceOrderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200));

            verify(notApproveBudgetUseCase).execute(serviceOrderId);
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Orçamento")
    class FindBudgetTests {

        @Test
        @DisplayName("Deve retornar um orçamento quando existir")
        void findBudget_shouldReturnBudget_whenExists() throws Exception {
            // given
            Long serviceOrderId = 1L;
            BudgetDetailOutputDTO outputDTO = createBudgetDetailOutputDTO();
            BudgetDetailResponseDTO responseDTO = createBudgetDetailResponseDTO();

            when(findBudgetUseCase.execute(serviceOrderId)).thenReturn(outputDTO);

            try (MockedStatic<BudgetMapper> mockedStatic = mockStatic(BudgetMapper.class)) {
                mockedStatic.when(() -> BudgetMapper.mapToResponseDTO(outputDTO))
                        .thenReturn(responseDTO);

                // when/then
                mockMvc.perform(get("/v1/budgets/{serviceOrderId}", serviceOrderId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value(200));

                verify(findBudgetUseCase).execute(serviceOrderId);
            }
        }

        @Test
        @DisplayName("Deve retornar status 404 (Not Found) quando o orçamento não existir")
        void findBudget_shouldReturnNotFound_whenBudgetDoesNotExist() throws Exception {
            // given
            Long serviceOrderId = 1L;
            when(findBudgetUseCase.execute(serviceOrderId)).thenThrow(new NotFoundException("budget.not.found"));

            // when/then
            mockMvc.perform(get("/v1/budgets/{serviceOrderId}", serviceOrderId))
                    .andExpect(status().isNotFound());

            verify(findBudgetUseCase).execute(serviceOrderId);
        }
    }

    private BudgetInputDTO createBudgetInputDTO() {
        return new BudgetInputDTO(
                1L,
                1L,
                1L,
                "Notas de teste",
                List.of()
        );
    }

    private BudgetDetailOutputDTO createBudgetDetailOutputDTO() {
        final var output = new BudgetOutputDTO(1L, 1L, 1, BigDecimal.valueOf(100), BudgetStatus.PENDING_APPROVAL, "Notas de teste", LocalDateTime.now(), null);
        final var item = new BudgetItemOutputDTO(1L, ItemTypeEnum.PART, 1L, 1L, 1, BigDecimal.valueOf(100));

        return new BudgetDetailOutputDTO(
                output,
                List.of(item)
        );
    }

    private BudgetDetailResponseDTO createBudgetDetailResponseDTO() {
        final var output = new BudgetResponseDTO(1L, 1L, 1, BigDecimal.valueOf(100), BudgetStatus.PENDING_APPROVAL, "Notas de teste", LocalDateTime.now(), null);
        final var item = new BudgetItemResponseDTO(1L, ItemTypeEnum.PART, 1L, 1L, 1, BigDecimal.valueOf(100));

        return new BudgetDetailResponseDTO(
                output,
                List.of(item)
        );
    }
}