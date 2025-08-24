package dev.com.soat.autorepairshop.application.models.input;

public record CreateVehicleInputDTO(
        String licensePlate,
        String brand,
        String model,
        Integer manufactureYear,
        String vehicleType,
        String carBodyType,
        String motorcycleStyleType,
        String color,
        String clientDocument
) {
}
