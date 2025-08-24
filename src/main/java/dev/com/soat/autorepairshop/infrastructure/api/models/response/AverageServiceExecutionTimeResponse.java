package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AverageServiceExecutionTimeResponse(
    @JsonProperty("total_seconds")
    double seconds,
    @JsonProperty("total_minutes")
    double minutes,
    @JsonProperty("total_hours")
    double hours,
    @JsonProperty("formatted_time")
    String formattedTime
) {}
