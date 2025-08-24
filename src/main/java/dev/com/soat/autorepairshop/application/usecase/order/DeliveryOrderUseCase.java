package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryOrderUseCase {
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;

    public void execute(final Long orderId){
        try {
            log.info("c=DeliveryOrderUseCase m=execute s=Start orderId={}", orderId);
            final var order = orderGateway.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("order.not.found"));

            if (order.isOrderInNotFinished()){
                throw new DomainException("order.is.not.finish");
            }

            order.delivery();
            final var history = ApplicationOrderHistoryMapper.map(order);

            orderGateway.save(order);
            orderHistoryGateway.save(history);
            log.info("c=DeliveryOrderUseCase m=execute s=Done orderId={}", orderId);
        } catch (Exception exception) {
            log.error("c=DeliveryOrderUseCase m=execute s=Error orderId={} message={}", orderId, exception.getMessage());
            throw exception;
        }
    }
}
