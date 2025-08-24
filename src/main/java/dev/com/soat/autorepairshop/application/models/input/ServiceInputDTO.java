package dev.com.soat.autorepairshop.application.models.input;

import java.math.BigDecimal;

public record ServiceInputDTO(String name, String description, BigDecimal basePrice) {
}
