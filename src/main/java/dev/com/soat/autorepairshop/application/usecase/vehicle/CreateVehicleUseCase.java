package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateVehicleUseCase {

    private final VehicleGateway vehicleGateway;

    private final VehicleValidationUtils vehicleValidationUtils;

    private final ClientValidationUtils clientValidationUtils;

    public VehicleOutputDTO execute(CreateVehicleInputDTO dto) {
        log.info("c=CreateVehicleUseCase m=execute s=start licensePlate = {}", dto.licensePlate());

        clientValidationUtils.validateClientExistenceByDocument(dto.clientDocument());

        vehicleValidationUtils.validateNewLicensePlate(dto.licensePlate());

        VehicleDomain vehicleDomain = VehicleApplicationMapper.toDomain(dto);

        VehicleDomain savedVehicle = vehicleGateway.save(vehicleDomain);

        log.info("c=CreateVehicleUseCase m=execute s=done licensePlate = {}", dto.licensePlate());

        return VehicleApplicationMapper.toDTO(savedVehicle);
    }
}
