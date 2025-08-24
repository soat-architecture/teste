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
public class ReadVehicleUseCase {

    private final VehicleGateway vehicleGateway;

    private final VehicleValidationUtils vehicleValidationUtils;

    public VehicleOutputDTO execute(String licensePlate) {
        log.info("c=ReadVehicleUseCase m=execute s=start licensePlate = {}", licensePlate);

        VehicleDomain vehicleDomain = vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate);

        log.info("c=ReadVehicleUseCase m=execute s=done licensePlate = {}", licensePlate);

        return VehicleApplicationMapper.toDTO(vehicleDomain);
    }

}
