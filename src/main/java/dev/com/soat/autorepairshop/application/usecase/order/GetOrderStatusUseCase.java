package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetOrderStatusUseCase {

    private final OrderGateway orderGateway;

    public OrderStatusType execute(Long id) {
        OrderDomain order = orderGateway.findById(id)
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        return order.getStatus();
    }
}
