package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class VehicleValidationUtils {

    private final VehicleGateway vehicleGateway;

    public VehicleDomain validateVehicleExistenceByLicensePlate(String licensePlate) {
        return vehicleGateway.findVehicleByLicensePlate(licensePlate).orElseThrow(() -> {
            log.error("c=VehicleValidationUtils m=validateVehicleExistence s=notFound licensePlate = {}", licensePlate);
            return new NotFoundException("vehicle.not.found");
        });
    }

    public LicensePlate validateNewLicensePlate(String licensePlate) {
        if (vehicleGateway.findVehicleByLicensePlate(licensePlate).isPresent()) {
            log.error("c=VehicleValidationUtils m=validateNewLicensePlate s=duplicate licensePlate = {}", licensePlate);
            throw new ConflictException("licenseplate.already.exists");
        }

        return new LicensePlate(licensePlate);
    }
}
