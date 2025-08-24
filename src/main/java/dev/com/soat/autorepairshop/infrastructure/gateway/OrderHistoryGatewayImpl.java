package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.infrastructure.repository.OrderHistoryRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.OrderHistoryEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.OrderHistoryEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderHistoryGatewayImpl implements OrderHistoryGateway {

    private final OrderHistoryRepository orderHistoryRepository;

    @Override
    public void save(OrderHistoryDomain orderHistory) {
        OrderHistoryEntity entity = OrderHistoryEntityMapper.toEntity(orderHistory);
        orderHistoryRepository.save(entity);
    }

    @Override
    public List<OrderHistoryDomain> findAllByOrderId(Long orderId) {
        List<OrderHistoryEntity> entities = orderHistoryRepository.findAllByOrderId(orderId);

        return entities.stream()
                .map(OrderHistoryEntityMapper::toDomain)
                .toList();
    }


}
