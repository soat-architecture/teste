package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;

public class VehicleEntityMapper {

    public static VehicleDomain toDomain(VehicleEntity entity){
        return VehicleDomain.restore(
                entity.getId(),
                entity.getLicensePlate(),
                entity.getBrand(),
                entity.getModel(),
                entity.getManufactureYear(),
                entity.getVehicleType(),
                entity.getCarBodyType(),
                entity.getMotorcycleStyleType(),
                entity.getColor(),
                entity.getDocument(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }

    public static VehicleEntity toEntity(VehicleDomain domain){
        String carBodyType = domain.getVehicleType().getCarBodyType() != null ? domain.getVehicleType().getCarBodyType().getName() : null;
        String motorcycleStyletype = domain.getVehicleType().getMotorcycleStyleType() != null ? domain.getVehicleType().getMotorcycleStyleType().getName() : null;

        return new VehicleEntity(
                domain.getIdentifier(),
                domain.getLicensePlate().getValue(),
                domain.getBrand(),
                domain.getModel(),
                domain.getManufactureYear().getValue(),
                domain.getVehicleType().getValue(),
                carBodyType,
                motorcycleStyletype,
                domain.getColor(),
                domain.getDocument().getValue(),
                domain.isActive()
                );
    }

}
