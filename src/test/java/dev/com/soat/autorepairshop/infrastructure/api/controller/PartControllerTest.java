package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.com.soat.autorepairshop.application.models.output.PartOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.part.*;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.utils.PageInfoGenericUtils;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.PartRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PartControllerTest {

    private MockMvc mockMvc;

    @Mock private CreatePartUseCase createUseCase;
    @Mock private UpdatePartUseCase updateUseCase;
    @Mock private DeletePartUseCase deleteUseCase;
    @Mock private FindPartUseCase findUseCase;
    @Mock private FindAllPartPageableUseCase findAllUseCase;

    @InjectMocks
    private PartController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private PartRequestDTO partRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();

        partRequest = new PartRequestDTO("Pastilha", "SKU001", "desc", "Bosch",
                new BigDecimal("100.00"), new BigDecimal("80.00"));
    }

    @Nested
    @DisplayName("POST /v1/parts")
    class CreateTests {
        @Test
        void shouldReturnCreated() throws Exception {
            doNothing().when(createUseCase).execute(any());

            mockMvc.perform(post("/v1/parts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partRequest)))
                    .andExpect(status().isCreated());
        }

        @Test
        void shouldReturnConflict() throws Exception {
            doThrow(new ConflictException("part.already.exists"))
                    .when(createUseCase).execute(any());

            mockMvc.perform(post("/v1/parts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /v1/parts/{id}")
    class ReadTests {
        @Test
        void shouldReturnOk() throws Exception {
            when(findUseCase.execute(anyLong())).thenReturn(mock(PartOutputDTO.class));

            mockMvc.perform(get("/v1/parts/{id}", 1))
                    .andExpect(status().isOk());
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            when(findUseCase.execute(anyLong()))
                    .thenThrow(new NotFoundException("part.not.found"));

            mockMvc.perform(get("/v1/parts/{id}", 1))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /v1/parts/all")
    class FindAllTests {
        @Test
        void shouldReturnOkWithPagination() throws Exception {
            var pageInfo = new PageInfoGenericUtils<PartOutputDTO>(List.of(), 0, 10, 0, 1);
            when(findAllUseCase.execute(0, 10, "name", "asc"))
                    .thenReturn(pageInfo);

            mockMvc.perform(get("/v1/parts/all")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "name")
                            .param("direction", "asc"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PUT /v1/parts/{id}")
    class UpdateTests {
        @Test
        void shouldReturnNoContent() throws Exception {
            doNothing().when(updateUseCase).execute(eq(1L), any());

            mockMvc.perform(put("/v1/parts/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partRequest)))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnConflict() throws Exception {
            doThrow(new ConflictException("part.sku.already.exists"))
                    .when(updateUseCase).execute(anyLong(), any());

            mockMvc.perform(put("/v1/parts/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(partRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /v1/parts/{id}")
    class DeleteTests {

        @Test
        void shouldReturnNoContent() throws Exception {
            doNothing().when(deleteUseCase).execute(anyLong());

            mockMvc.perform(delete("/v1/parts/{id}", 1))
                    .andExpect(status().isNoContent());
        }

        @Test
        void shouldReturnNotFound() throws Exception {
            doThrow(new NotFoundException("part.not.found"))
                    .when(deleteUseCase).execute(anyLong());

            mockMvc.perform(delete("/v1/parts/{id}", 1))
                    .andExpect(status().isNotFound());
        }
    }
}
