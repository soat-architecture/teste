package dev.com.soat.autorepairshop.application.models.input;

public record OrderInputDTO(
    String clientDocument,
    String vehicleLicensePlate,
    Long employeeIdentifier,
    Long serviceId,
    String notes
) {
}
