package dev.com.soat.autorepairshop.application.models.output;

import java.time.LocalDateTime;

public record OrderOutputDTO(
        Long orderId,
        String clientDocument,
        String vehicleLicensePlate,
        String status,
        Long employeeIdentifier,
        Long serviceId,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {
}
