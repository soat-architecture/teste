package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;

import java.util.List;
import java.util.Optional;

public interface VehicleGateway {

    VehicleDomain save(VehicleDomain vehicleDomain);

    Optional<VehicleDomain> findVehicleByLicensePlate(String licensePlate);

    List<VehicleDomain> findVehiclesByOwner(String document);

    Optional<VehicleDomain> findById(Long id);

    void delete(String licensePlate);
}
