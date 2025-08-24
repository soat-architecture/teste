package dev.com.soat.autorepairshop.application.models.input;

import dev.com.soat.autorepairshop.domain.enums.MovementType;

public record InventoryMovementInputDTO(Long partId,
                                        Long userId,
                                        Long serviceOrderId,
                                        MovementType movementType,
                                        Integer quantityChanged,
                                        Integer quantityBefore,
                                        Integer quantityAfter,
                                        String reason) {}