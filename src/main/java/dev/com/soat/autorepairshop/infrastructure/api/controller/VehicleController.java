package dev.com.soat.autorepairshop.infrastructure.api.controller;

import dev.com.soat.autorepairshop.application.models.input.UpdateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehiclesOwnerOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.vehicle.*;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.VehicleMapper;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.CreateVehicleRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UpdateVehicleRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultDataDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.VehicleResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.VehiclesOwnerResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static dev.com.soat.autorepairshop.infrastructure.api.models.utils.OpenAPI.VEHICLES_TAG;

@Tag(name = VEHICLES_TAG, description = "Operações relacionadas aos veículos da plataforma.")
@RestController
@RequestMapping("/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final CreateVehicleUseCase createVehicleUseCase;
    private final ReadVehicleUseCase readVehicleUseCase;
    private final UpdateVehicleUseCase updateVehicleUseCase;
    private final DeleteVehicleUseCase deleteVehicleUseCase;
    private final FindOwnerVehiclesUseCase findOwnerVehiclesUseCase;

    @PostMapping
    @Operation(
            summary = "Criação de um novo veículo",
            description = "Endpoint responsável por cadastrar de um veículo na base de dados.",
            requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados do veículo",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Veículo cadastrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos!"),
                    @ApiResponse(responseCode = "404", description = "Dono do veículo não encontrado."),
                    @ApiResponse(responseCode = "409", description = "Veículo já existe."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    public ResponseEntity<VehicleResponseDTO> create(@Valid @RequestBody CreateVehicleRequestDTO requestDTO) {
        CreateVehicleInputDTO inputDTO = VehicleMapper.map(requestDTO);
        VehicleOutputDTO outputDTO = createVehicleUseCase.execute(inputDTO);
        VehicleResponseDTO responseDTO = VehicleMapper.map(outputDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping("/{licensePlate}")
    @Operation(
            summary = "Busca de veículo pela placa.",
            description = "Endpoint responsável por buscar um veículo na base de dados através da placa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Placa em formato inválido!"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    public ResponseEntity<VehicleResponseDTO> read(@PathVariable String licensePlate) {
        VehicleOutputDTO outputDTO = readVehicleUseCase.execute(licensePlate);
        VehicleResponseDTO responseDTO = VehicleMapper.map(outputDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{licensePlate}")
    @Operation(
            summary = "Atualização de um veículo pela placa.",
            description = "Endpoint responsável pela atualização de um veículo na base de dados através da sua placa.",
            requestBody =  @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição contendo os dados do veículo a serem atualizados.",
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo atualizado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Placa em formato inválido!"),
                    @ApiResponse(responseCode = "404", description = "Veículo ou novo dono não encontrado."),
                    @ApiResponse(responseCode = "409", description = "Nova placa já existe."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    public ResponseEntity<VehicleResponseDTO> update(@PathVariable String licensePlate, @Valid @RequestBody UpdateVehicleRequestDTO requestDTO) {
        UpdateVehicleInputDTO inputDTO = VehicleMapper.map(requestDTO);
        VehicleOutputDTO outputDTO = updateVehicleUseCase.execute(inputDTO, licensePlate);
        VehicleResponseDTO responseDTO = VehicleMapper.map(outputDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{licensePlate}")
    @Operation(
            summary = "Exclusão lógica de um veículo pela placa.",
            description = "Endpoint responsável por realizar a exclusão lógica de um veículo na base de dados através da sua placa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo excluído com sucesso."),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
                    @ApiResponse(responseCode = "409", description = "Veículo já foi desativado."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    public DefaultResponseDTO<DefaultDataDTO> delete(@PathVariable String licensePlate) {
        deleteVehicleUseCase.execute(licensePlate);
        return DefaultResponseDTO.ok();
    }

    @GetMapping("/owner/{document}")
    @Operation(
            summary = "Busca de veículo pela placa.",
            description = "Endpoint responsável por buscar um veículo na base de dados através da placa.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Veículo encontrado com sucesso."),
                    @ApiResponse(responseCode = "400", description = "Placa em formato inválido!"),
                    @ApiResponse(responseCode = "404", description = "Veículo não encontrado."),
                    @ApiResponse(responseCode = "500", description = "Erro desconhecido.")
            }
    )
    public ResponseEntity<VehiclesOwnerResponseDTO> readOwnerVehicles(@PathVariable String document) {
        VehiclesOwnerOutputDTO outputDTO = findOwnerVehiclesUseCase.execute(document);
        VehiclesOwnerResponseDTO responseDTO = VehicleMapper.map(outputDTO);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }
}
