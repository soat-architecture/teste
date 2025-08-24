package dev.com.soat.autorepairshop.infrastructure.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.input.UpdateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehiclesOwnerOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.vehicle.CreateVehicleUseCase;
import dev.com.soat.autorepairshop.application.usecase.vehicle.DeleteVehicleUseCase;
import dev.com.soat.autorepairshop.application.usecase.vehicle.FindOwnerVehiclesUseCase;
import dev.com.soat.autorepairshop.application.usecase.vehicle.ReadVehicleUseCase;
import dev.com.soat.autorepairshop.application.usecase.vehicle.UpdateVehicleUseCase;
import dev.com.soat.autorepairshop.infrastructure.adapter.LocalDateTimeAdapter;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.VehicleMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.CreateVehicleRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UpdateVehicleRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    private MockMvc mockMvc;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @Mock
    private CreateVehicleUseCase createVehicleUseCase;

    @Mock
    private ReadVehicleUseCase readVehicleUseCase;

    @Mock
    private UpdateVehicleUseCase updateVehicleUseCase;

    @Mock
    private DeleteVehicleUseCase deleteVehicleUseCase;

    @Mock
    private FindOwnerVehiclesUseCase findOwnerVehiclesUseCase;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(vehicleController)
                .setControllerAdvice(new dev.com.soat.autorepairshop.infrastructure.exception.GlobalExceptionHandler(mock(org.springframework.context.MessageSource.class)))
                .build();
    }

    @Test
    @DisplayName("Deve criar um veículo com sucesso e retornar 201 Created")
    void testCreate() throws Exception {
        // Given
        LocalDateTime createdAt = LocalDateTime.of(2025, Month.JULY, 10, 0, 0);

        CreateVehicleRequestDTO requestDTO = new CreateVehicleRequestDTO(
                "ABC-1234", "Ford", "Focus", 2017, "Carro", "Hatch", null, "Preto", "45997418000153");

        CreateVehicleInputDTO inputDTO = VehicleMapper.map(requestDTO);

        VehicleOutputDTO outputDTO = new VehicleOutputDTO(
                "ABC-1234", "Ford", "Focus", 2017, "Carro", "Hatch", null, "Preto",
                "45997418000153", createdAt, null, true);

        when(createVehicleUseCase.execute(inputDTO)).thenReturn(outputDTO);

        mockMvc.perform(post("/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.brand").value("Ford"))
                .andExpect(jsonPath("$.model").value("Focus"))
                .andExpect(jsonPath("$.manufactureYear").value(2017))
                .andExpect(jsonPath("$.vehicleType").value("Carro"))
                .andExpect(jsonPath("$.carBodyType").value("Hatch"))
                .andExpect(jsonPath("$.motorcycleStyleType").doesNotExist())
                .andExpect(jsonPath("$.color").value("Preto"))
                .andExpect(jsonPath("$.document").value("45997418000153"))
                .andExpect(jsonPath("$.createdAt").value("2025/07/10 00:00"))
                .andExpect(jsonPath("$.updatedAt").doesNotExist())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Deve buscar um veículo com sucesso e retornar 200 OK")
    void testRead() throws Exception {
        // Given
        String licensePlate = "ABC-1234";
        LocalDateTime createdAt = LocalDateTime.of(2025, Month.JANUARY, 22, 10, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2025, Month.JUNE, 15, 14, 0);

        VehicleOutputDTO outputDTO = new VehicleOutputDTO(
                licensePlate, "Fiat", "Cronos", 2022, "Carro", "Sedan", null, "Prata",
                "45997418000153", createdAt, updatedAt, true);

        when(readVehicleUseCase.execute(licensePlate)).thenReturn(outputDTO);

        mockMvc.perform(get("/v1/vehicles/{licensePlate}", licensePlate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.brand").value("Fiat"))
                .andExpect(jsonPath("$.model").value("Cronos"))
                .andExpect(jsonPath("$.manufactureYear").value(2022))
                .andExpect(jsonPath("$.vehicleType").value("Carro"))
                .andExpect(jsonPath("$.carBodyType").value("Sedan"))
                .andExpect(jsonPath("$.motorcycleStyleType").doesNotExist())
                .andExpect(jsonPath("$.color").value("Prata"))
                .andExpect(jsonPath("$.document").value("45997418000153"))
                .andExpect(jsonPath("$.createdAt").value("2025/01/22 10:30"))
                .andExpect(jsonPath("$.updatedAt").value("2025/06/15 14:00"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Deve atualizar um veículo com sucesso e retornar 200 OK")
    void testUpdate() throws Exception {
        // Given
        String licensePlate = "ABC-1234";
        String newLicensePlate = "DEF-5678";

        LocalDateTime createdAt = LocalDateTime.of(2025, Month.MARCH, 1, 9, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, Month.JULY, 19, 10, 0);

        UpdateVehicleRequestDTO requestDTO = new UpdateVehicleRequestDTO(newLicensePlate, "", "", false);

        UpdateVehicleInputDTO inputDTO = VehicleMapper.map(requestDTO);

        VehicleOutputDTO outputDTO = new VehicleOutputDTO(
                newLicensePlate, "Toyota", "Corolla", 2020, "Carro", "Sedan", null, "Azul",
                "45997418000153", createdAt, updatedAt, true);

        when(updateVehicleUseCase.execute(inputDTO, licensePlate)).thenReturn(outputDTO);

        mockMvc.perform(put("/v1/vehicles/{licensePlate}", licensePlate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value(newLicensePlate))
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.manufactureYear").value(2020))
                .andExpect(jsonPath("$.vehicleType").value("Carro"))
                .andExpect(jsonPath("$.carBodyType").value("Sedan"))
                .andExpect(jsonPath("$.motorcycleStyleType").doesNotExist())
                .andExpect(jsonPath("$.color").value("Azul"))
                .andExpect(jsonPath("$.document").value("45997418000153"))
                .andExpect(jsonPath("$.createdAt").value("2025/03/01 09:00"))
                .andExpect(jsonPath("$.updatedAt").value("2025/07/19 10:00"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("Deve deletar um veículo com sucesso e retornar 200 OK")
    void testDelete() throws Exception {
        String licensePlate = "ABC-1234";

        doNothing().when(deleteVehicleUseCase).execute(licensePlate);

        mockMvc.perform(delete("/v1/vehicles/{licensePlate}", licensePlate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve buscar veículos do proprietário com sucesso e retornar 200 OK")
    void testReadOwnerVehicles() throws Exception {
        String document = "45997418000153";
        LocalDateTime createdAt = LocalDateTime.of(2025, Month.JANUARY, 22, 10, 30);
        LocalDateTime updatedAt = LocalDateTime.of(2025, Month.JUNE, 15, 14, 0);

        VehicleOutputDTO vehicleOutputDTO = new VehicleOutputDTO(
                "ABC-1234", "Toyota", "Corolla", 2020, "Carro", "Sedan", null, "Azul",
                document, createdAt, updatedAt, true);

        VehiclesOwnerOutputDTO vehiclesOwnerOutputDTO = new VehiclesOwnerOutputDTO(document, List.of(vehicleOutputDTO));

        when(findOwnerVehiclesUseCase.execute(document)).thenReturn(vehiclesOwnerOutputDTO);

        mockMvc.perform(get("/v1/vehicles/owner/{document}", document)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("45997418000153"))
                .andExpect(jsonPath("$.vehicles[0].licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.vehicles[0].brand").value("Toyota"))
                .andExpect(jsonPath("$.vehicles[0].model").value("Corolla"))
                .andExpect(jsonPath("$.vehicles[0].manufactureYear").value(2020))
                .andExpect(jsonPath("$.vehicles[0].vehicleType").value("Carro"))
                .andExpect(jsonPath("$.vehicles[0].carBodyType").value("Sedan"))
                .andExpect(jsonPath("$.vehicles[0].motorcycleStyleType").doesNotExist())
                .andExpect(jsonPath("$.vehicles[0].color").value("Azul"))
                .andExpect(jsonPath("$.vehicles[0].document").value("45997418000153"))
                .andExpect(jsonPath("$.vehicles[0].createdAt").value("2025/01/22 10:30"))
                .andExpect(jsonPath("$.vehicles[0].updatedAt").value("2025/06/15 14:00"))
                .andExpect(jsonPath("$.vehicles[0].active").value(true));
    }
}