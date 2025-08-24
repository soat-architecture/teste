package dev.com.soat.autorepairshop.application.usecase.order.dto;

public record AverageServiceExecutionTimeDTO(
        double seconds,
        double minutes,
        double hours,
        String formattedTime
) {
    public static AverageServiceExecutionTimeDTO empty() {
        return new AverageServiceExecutionTimeDTO(0, 0, 0, "00:00:00");
    }
}
