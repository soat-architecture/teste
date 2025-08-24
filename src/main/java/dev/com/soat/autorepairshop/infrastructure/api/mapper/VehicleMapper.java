package dev.com.soat.autorepairshop.infrastructure.api.mapper;

import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.input.UpdateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehiclesOwnerOutputDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.CreateVehicleRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.request.UpdateVehicleRequestDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.VehicleResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.VehiclesOwnerResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleMapper {

    public static CreateVehicleInputDTO map(CreateVehicleRequestDTO dto){
        return new CreateVehicleInputDTO(
                dto.licensePlate(),
                dto.brand(),
                dto.model(),
                dto.manufactureYear(),
                dto.vehicleType(),
                dto.carBodyType(),
                dto.motorcycleStyleType(),
                dto.color(),
                dto.document()
        );
    }

    public static VehicleResponseDTO map(VehicleOutputDTO dto){
        return new VehicleResponseDTO(
                dto.licensePlate(),
                dto.brand(),
                dto.model(),
                dto.manufactureYear(),
                dto.vehicleType(),
                dto.carBodyType(),
                dto.motorcycleStyleType(),
                dto.color(),
                dto.document(),
                dto.createdAt(),
                dto.updatedAt(),
                dto.active()
        );
    }

    public static UpdateVehicleInputDTO map(UpdateVehicleRequestDTO dto) {
        return new UpdateVehicleInputDTO(
                dto.newLicensePlate(),
                dto.newDocument(),
                dto.newColor(),
                dto.active()
        );
    }

    public static VehiclesOwnerResponseDTO map(VehiclesOwnerOutputDTO dto) {
        List<VehicleResponseDTO> vehicles = dto.vehicles().stream().map(VehicleMapper::map).collect(Collectors.toList());

        return new VehiclesOwnerResponseDTO(
                dto.document(),
                vehicles
        );
    }
}
