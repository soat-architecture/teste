package dev.com.soat.autorepairshop.application.models.input;

import java.math.BigDecimal;

public record PartInputDTO(
        String name,
        String sku,
        String description,
        String brand,
        BigDecimal sellingPrice,
        BigDecimal buyPrice) {
}
