package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.mapper.InventoryApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.InventoryInputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryMovementOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.InventoryOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.inventory.CreateInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.FindInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.UpdateInventoryUseCase;
import dev.com.soat.autorepairshop.application.usecase.inventory.movements.FindInventoryMovementsUseCase;
import dev.com.soat.autorepairshop.domain.enums.MovementType;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.InventoryMovementMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.InventoryUpdateRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FindInventoryUseCase findUseCase;

    @Mock
    private CreateInventoryUseCase createUseCase;

    @Mock
    private UpdateInventoryUseCase updateUseCase;

    @Mock
    private FindInventoryMovementsUseCase findInventoryMovementsUseCase;

    @InjectMocks
    private InventoryController controller;

    private InventoryRequestDTO requestDTO;
    private InventoryInputDTO inventoryInputDTO;
    private InventoryOutputDTO outputDTO;
    private InventoryUpdateRequestDTO updateRequestDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(
                        mock(org.springframework.context.MessageSource.class)))
                .build();

        requestDTO = new InventoryRequestDTO(1L, 100);
        inventoryInputDTO = new InventoryInputDTO(1L, 100, MovementType.INITIAL);
        outputDTO = new InventoryOutputDTO(1L, 100);
        updateRequestDTO = new InventoryUpdateRequestDTO(50, MovementType.INITIAL);
    }

    @Test
    @DisplayName("Deve ler o inventário pelo ID da peça")
    void shouldReadInventoryByPartId() {
        when(findUseCase.execute(1L)).thenReturn(outputDTO);

        try (MockedStatic<InventoryApplicationMapper> mocked = mockStatic(InventoryApplicationMapper.class)) {
            mocked.when(() -> InventoryApplicationMapper.map(outputDTO)).thenReturn(requestDTO);

            DefaultResponseDTO<InventoryRequestDTO> response = controller.read(1L);

            assertEquals(200, response.status());
            assertNotNull(response.data());
            assertEquals(1L, response.data().partId());
            assertEquals(100, response.data().quantityOnHand());
        }

        verify(findUseCase).execute(1L);
    }

    @Test
    @DisplayName("Deve retornar histórico de movimentações")
    void shouldReturnMovementHistory() {
        InventoryMovementOutputDTO output = mock(InventoryMovementOutputDTO.class);
        when(findInventoryMovementsUseCase.execute("1234")).thenReturn(List.of(output));

        DefaultResponseDTO<List<InventoryMovementOutputDTO>> response =
                controller.readMovementHistory("1234");

        assertEquals(200, response.status());
        assertNotNull(response.data());
        assertEquals(1, response.data().size());

        verify(findInventoryMovementsUseCase).execute("1234");
    }

    @Test
    @DisplayName("Deve criar novo inventário")
    void shouldCreateInventory() {
        try (MockedStatic<InventoryApplicationMapper> mocked = mockStatic(InventoryApplicationMapper.class)) {
            mocked.when(() -> InventoryApplicationMapper.map(requestDTO)).thenReturn(inventoryInputDTO);

            DefaultResponseDTO<InventoryRequestDTO> response = controller.create(requestDTO);

            assertEquals(201, response.status());
            assertNotNull(response.data());
            assertEquals(requestDTO, response.data());
        }

        verify(createUseCase).execute(inventoryInputDTO);
    }

    @Test
    @DisplayName("Deve atualizar inventário existente")
    void shouldUpdateInventory() {
        Long partId = 1L;
        try (MockedStatic<InventoryMovementMapper> mocked = mockStatic(InventoryMovementMapper.class)) {
            mocked.when(() -> InventoryMovementMapper.map(eq(partId), any(InventoryUpdateRequestDTO.class)))
                    .thenReturn(inventoryInputDTO);

            DefaultResponseDTO<InventoryInputDTO> response = controller.update(partId, updateRequestDTO);

            assertEquals(200, response.status());
            assertNotNull(response.data());
            assertEquals(inventoryInputDTO, response.data());
        }

        verify(updateUseCase).execute(eq(partId), any(InventoryInputDTO.class));
    }

    @Test
    @DisplayName("Deve validar chamada REST para leitura de inventário")
    void shouldValidateRestCallForReadInventory() throws Exception {
        when(findUseCase.execute(1L)).thenReturn(outputDTO);

        mockMvc.perform(get("/v1/inventory/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(findUseCase).execute(1L);
    }

    @Test
    @DisplayName("Deve validar chamada REST para histórico de movimentações")
    void shouldValidateRestCallForMovementHistory() throws Exception {
        String partSku = "SKU123";
        when(findInventoryMovementsUseCase.execute(partSku))
                .thenReturn(List.of(mock(InventoryMovementOutputDTO.class)));

        mockMvc.perform(get("/v1/inventory/{partSku}/movements", partSku)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(findInventoryMovementsUseCase).execute(partSku);
    }

    @Test
    @DisplayName("Deve validar chamada REST para criação de inventário")
    void shouldValidateRestCallForCreateInventory() throws Exception {
        String requestBody = """
                {
                    "partId": 1,
                    "quantityOnHand": 100
                }
                """;

        mockMvc.perform(post("/v1/inventory/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(createUseCase).execute(any(InventoryInputDTO.class));
    }
}