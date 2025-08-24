package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteVehicleUseCase {

    private final VehicleGateway gateway;

    private final VehicleValidationUtils vehicleValidationUtils;

    public void execute(String licensePlate) {
        log.info("c=DeleteVehicleUseCase m=execute s=start licensePlate = {}", licensePlate);

        vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate);

        gateway.delete(licensePlate);

        log.info("c=DeleteVehicleUseCase m=execute s=done licensePlate = {}", licensePlate);
    }
}
