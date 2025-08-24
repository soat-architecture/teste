package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderMapper;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindOrderUseCase {
    private final OrderGateway gateway;

    public OrderOutputDTO execute(Long orderId){
        OrderDomain orderDomain = gateway.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order.not.found"));

        return ApplicationOrderMapper.toDTO(orderDomain);
    }
}
