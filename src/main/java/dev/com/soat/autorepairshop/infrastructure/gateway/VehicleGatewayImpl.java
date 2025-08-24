package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.VehicleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.VehicleEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VehicleGatewayImpl implements VehicleGateway {

    private final VehicleRepository vehicleRepository;

    @Override
    public VehicleDomain save(VehicleDomain vehicleDomain) {
        VehicleEntity entity = VehicleEntityMapper.toEntity(vehicleDomain);
        VehicleEntity saved = vehicleRepository.save(entity);
        return VehicleEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<VehicleDomain> findVehicleByLicensePlate(String licensePlate) {
        VehicleEntity vehicleEntity = vehicleRepository.findVehicleByLicensePlate(licensePlate);

        if (vehicleEntity != null) {
            VehicleDomain vehicleDomain = VehicleEntityMapper.toDomain(vehicleEntity);
            return Optional.of(vehicleDomain);
        }

        return Optional.empty();
    }

    @Override
    public List<VehicleDomain> findVehiclesByOwner(String document) {
        List<VehicleEntity> vehiclesEntity = vehicleRepository.findVehicleByOwner(document);
        return vehiclesEntity.stream().map(VehicleEntityMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<VehicleDomain> findById(Long id) {
        Optional<VehicleEntity> optionalVehicleEntity = vehicleRepository.findById(id);

        if (optionalVehicleEntity.isPresent()) {
            VehicleDomain vehicleDomain = VehicleEntityMapper.toDomain(optionalVehicleEntity.get());
            return Optional.of(vehicleDomain);
        }

        return Optional.empty();
    }

    @Override
    public void delete(String licensePlate) {
        this.vehicleRepository.softDeleteByLicensePlate(licensePlate);
    }
}
