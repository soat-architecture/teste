package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.application.usecase.client.CreateClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.DeleteClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.FindClientUseCase;
import dev.com.soat.autorepairshop.application.usecase.client.UpdateClientUseCase;
import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.ClientRequestDTO;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateClientUseCase createUseCase;

    @Mock
    private FindClientUseCase findUseCase;

    @Mock
    private UpdateClientUseCase updateUseCase;

    @Mock
    private DeleteClientUseCase deleteUseCase;

    @InjectMocks
    private ClientController clientController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ClientRequestDTO clientRequestDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
        clientRequestDTO = new ClientRequestDTO("John Doe", "12345678901", "(11) 99999-9999", "john.doe@email.com", ClientStatus.ACTIVE);
    }

    @Nested
    @DisplayName("Testes de Criação de Cliente")
    class CreateClientTests {

        @Test
        @DisplayName("Deve criar um cliente e retornar status 201 (Created)")
        void createClient_shouldReturnCreated_whenClientIsValid() throws Exception {
            doNothing().when(createUseCase).execute(any(ClientInputDTO.class));

            mockMvc.perform(post("/v1/client")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientRequestDTO)))
                    .andExpect(status().isCreated());

            verify(createUseCase).execute(any(ClientInputDTO.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 (Bad Request) quando o payload for inválido")
        void createClient_shouldReturnBadRequest_whenPayloadIsInvalid() throws Exception {
            ClientRequestDTO invalidRequest = new ClientRequestDTO("", "12345678901", "(11) 99999-9999", "gusthawo@email.com", ClientStatus.ACTIVE);

            mockMvc.perform(post("/v1/client")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Cliente")
    class GetClientTests {

        @Test
        @DisplayName("Deve retornar um cliente quando o documento existir")
        void getClient_shouldReturnClient_whenDocumentExists() throws Exception {
            String document = "12345678901";
            ClientInputDTO clientInputDTO = new ClientInputDTO(1L, "Gusthawo Junkes", "12345678901", "(11) 99999-9999", "gusthawo@email.com", ClientStatus.ACTIVE);
            when(findUseCase.execute(document)).thenReturn(clientInputDTO);

            mockMvc.perform(get("/v1/client/{document}", document))
                    .andExpect(status().isOk());

            verify(findUseCase).execute(document);
        }

        @Test
        @DisplayName("Deve retornar status 404 (Not Found) quando o cliente não for encontrado")
        void getClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
            String document = "12345678901";
            when(findUseCase.execute(document)).thenThrow(new NotFoundException("client.not.found"));

            mockMvc.perform(get("/v1/client/{document}", document))
                    .andExpect(status().isNotFound());

            verify(findUseCase).execute(document);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Cliente")
    class UpdateClientTests {

        @Test
        @DisplayName("Deve atualizar um cliente e retornar status 200 (OK)")
        void updateClient_shouldReturnOk_whenClientIsValid() throws Exception {
            String document = "12345678901";
            doNothing().when(updateUseCase).execute(eq(document), any(ClientInputDTO.class));

            mockMvc.perform(put("/v1/client/{document}", document)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clientRequestDTO)))
                    .andExpect(status().isOk());

            verify(updateUseCase).execute(eq(document), any(ClientInputDTO.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 (Bad Request) quando o payload for inválido")
        void updateClient_shouldReturnBadRequest_whenPayloadIsInvalid() throws Exception {
            String document = "12345678901";
            ClientRequestDTO invalidRequest = new ClientRequestDTO("", "12345678901", "(11) 99999-9999", "gusthawo@email.com", ClientStatus.ACTIVE);

            mockMvc.perform(put("/v1/client/{document}", document)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão de Cliente")
    class DeleteClientTests {

        @Test
        @DisplayName("Deve excluir um cliente e retornar status 200 (OK)")
        void deleteClient_shouldReturnOk_whenDocumentExists() throws Exception {
            String document = "12345678901";
            doNothing().when(deleteUseCase).execute(document);

            mockMvc.perform(delete("/v1/client/{document}", document))
                    .andExpect(status().isOk());

            verify(deleteUseCase).execute(document);
        }

        @Test
        @DisplayName("Deve retornar status 404 (Not Found) quando o cliente não for encontrado")
        void deleteClient_shouldReturnNotFound_whenClientDoesNotExist() throws Exception {
            String document = "12345678901";
            doThrow(new NotFoundException("client.not.found")).when(deleteUseCase).execute(document);

            mockMvc.perform(delete("/v1/client/{document}", document))
                    .andExpect(status().isNotFound());

            verify(deleteUseCase).execute(document);
        }
    }
}
