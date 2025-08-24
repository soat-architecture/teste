package dev.com.soat.autorepairshop.application.mapper;

import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;

public class VehicleApplicationMapper {

    public static VehicleDomain toDomain(CreateVehicleInputDTO dto){
        return VehicleDomain.create(
                dto.licensePlate(),
                dto.brand(),
                dto.model(),
                dto.manufactureYear(),
                dto.vehicleType(),
                dto.carBodyType(),
                dto.motorcycleStyleType(),
                dto.color(),
                dto.clientDocument()
        );
    }

    public static VehicleOutputDTO toDTO(VehicleDomain domain){
        String carBody = domain.getVehicleType().getCarBodyType() != null ? domain.getVehicleType().getCarBodyType().getName() : null;
        String motorcycleStyle = domain.getVehicleType().getMotorcycleStyleType() != null ? domain.getVehicleType().getMotorcycleStyleType().getName() : null;

        return new VehicleOutputDTO(
                domain.getLicensePlate().getValue(),
                domain.getBrand(),
                domain.getModel(),
                domain.getManufactureYear().getValue(),
                domain.getVehicleType().getValue(),
                carBody,
                motorcycleStyle,
                domain.getColor(),
                domain.getDocument().getValue(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.isActive()
        );
    }

}
