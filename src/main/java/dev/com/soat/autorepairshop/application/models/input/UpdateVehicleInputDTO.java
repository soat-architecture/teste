package dev.com.soat.autorepairshop.application.models.input;

public record UpdateVehicleInputDTO(
        String newLicensePlate,
        String newDocument,
        String newColor,
        boolean active
) {
}
