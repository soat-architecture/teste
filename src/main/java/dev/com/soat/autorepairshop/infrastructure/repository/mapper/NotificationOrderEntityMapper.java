package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.enums.NotificationStatusType;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.NotificationOrderEntity;

import java.time.LocalDateTime;

public class NotificationOrderEntityMapper {
    private NotificationOrderEntityMapper() {

    }

    public static NotificationOrderEntity create(final Long serviceOrderId,
                                          final Long clientId,
                                          final String channel,
                                          final String message,
                                          final NotificationStatusType type){
        return new NotificationOrderEntity(
                null,
                serviceOrderId,
                clientId,
                channel,
                message,
                type,
                LocalDateTime.now()
        );
    }
}
