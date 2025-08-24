package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.application.utils.OrderValidationUtils;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StartOrderUseCase {
    private final OrderValidationUtils orderValidationUtils;
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;

    public void execute(final Long orderId, final Long employeeId){
        try {
            log.info("c=StartOrderUseCase m=execute s=Start orderId={} employeeId={}", orderId, employeeId);
            final var order = orderValidationUtils.validateOrderIsAssignedToEmployee(orderId, employeeId);

            if (order.isOrderInNotApproved()){
                throw new DomainException("order.is.not.approved");
            }

            order.startExecution();
            final var history = ApplicationOrderHistoryMapper.map(order);

            orderGateway.save(order);
            orderHistoryGateway.save(history);
            log.info("c=StartOrderUseCase m=execute s=Done orderId={} employeeId={}", orderId, employeeId);
        } catch (Exception exception) {
            log.error("c=StartOrderUseCase m=execute s=Error orderId={} employeeId={} message={}", orderId, employeeId, exception.getMessage());
            throw exception;
        }
    }
}
