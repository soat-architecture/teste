package dev.com.soat.autorepairshop.application.models.output;

import java.time.LocalDateTime;

public record VehicleOutputDTO(
        String licensePlate,
        String brand,
        String model,
        Integer manufactureYear,
        String vehicleType,
        String carBodyType,
        String motorcycleStyleType,
        String color,
        String document,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {
}
