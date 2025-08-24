package dev.com.soat.autorepairshop.application.models.output;

import java.util.List;

public record VehiclesOwnerOutputDTO(
        String document,
        List<VehicleOutputDTO> vehicles
) {
}
