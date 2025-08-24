package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehiclesOwnerOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.shared.masker.DocumentMasker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindOwnerVehiclesUseCase {

    private final VehicleGateway vehicleGateway;

    private final ClientGateway clientGateway;

    private final ClientValidationUtils clientValidationUtils;

    public VehiclesOwnerOutputDTO execute(String document){
        log.info("c=FindOwnerVehiclesUseCase m=execute s=start document = {}", DocumentMasker.mask(document));

        clientValidationUtils.validateClientExistenceByDocument(document);

        List<VehicleDomain> vehicles = vehicleGateway.findVehiclesByOwner(document);

        List<VehicleOutputDTO> vehiclesOutput = vehicles.stream().map(VehicleApplicationMapper::toDTO).collect(Collectors.toList());

        return new VehiclesOwnerOutputDTO(document, vehiclesOutput);
    }


}
