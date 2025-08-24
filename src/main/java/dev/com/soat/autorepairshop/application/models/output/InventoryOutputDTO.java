package dev.com.soat.autorepairshop.application.models.output;

public record InventoryOutputDTO(
    Long partId,
    Integer quantityOnHand
){ }