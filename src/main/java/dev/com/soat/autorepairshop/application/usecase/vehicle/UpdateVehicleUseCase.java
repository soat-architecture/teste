package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.UpdateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateVehicleUseCase {

    private final VehicleGateway vehicleGateway;

    private final VehicleValidationUtils vehicleValidationUtils;

    private final ClientGateway clientGateway;

    private final ClientValidationUtils clientValidationUtils;

    public VehicleOutputDTO execute(UpdateVehicleInputDTO dto, String actualLicensePlate) {
        log.info("c=UpdateVehicleUseCase m=execute s=start actualLicensePlate = {}", actualLicensePlate);

        VehicleDomain vehicleDomain = vehicleValidationUtils.validateVehicleExistenceByLicensePlate(actualLicensePlate);

        if (dto.newLicensePlate() != null && !dto.newLicensePlate().isBlank()) {
            LicensePlate newLicensePlate = vehicleValidationUtils.validateNewLicensePlate(dto.newLicensePlate());
            vehicleDomain.changeLicensePlate(newLicensePlate);
        }
        if (dto.newDocument() != null && !dto.newDocument().isBlank()) {
            clientValidationUtils.validateClientExistenceByDocument(dto.newDocument());
            Document newdocument = Document.from(dto.newDocument());
            vehicleDomain.changeOwner(newdocument);
        }
        if (dto.newColor() != null && !dto.newColor().isBlank())
            vehicleDomain.changeColor(dto.newColor());
        if (dto.active())
            vehicleDomain.activate();

        VehicleDomain updatedVehicle = vehicleGateway.save(vehicleDomain);

        log.info("c=UpdateVehicleUseCase m=execute s=done licensePlate = {}", actualLicensePlate);

        return VehicleApplicationMapper.toDTO(updatedVehicle);
    }

}
