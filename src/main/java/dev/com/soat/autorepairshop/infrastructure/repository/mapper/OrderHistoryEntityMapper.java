package dev.com.soat.autorepairshop.infrastructure.repository.mapper;

import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderHistoryEntity;

public class OrderHistoryEntityMapper {

    private OrderHistoryEntityMapper() {

    }

    public static OrderHistoryEntity toEntity(OrderHistoryDomain domain){
        return new OrderHistoryEntity(
                domain.getIdentifier(),
                domain.getOrderId(),
                domain.getOrderStatus(),
                domain.getNotes(),
                domain.getCreatedAt()
        );
    }

    public static OrderHistoryDomain toDomain(OrderHistoryEntity entity){
        return OrderHistoryDomain.restore(
                entity.getIdentifier(),
                entity.getOrderId(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }

}
