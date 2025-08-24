package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;

public record OrderStatusResponse(
    OrderStatusType status
) {}