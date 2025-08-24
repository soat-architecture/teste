package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.OrderHistoryGateway;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AssignmentOrderUseCase {
    private final UserGateway userGateway;
    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;

    public void execute(final Long orderId, final Long employeeId){
        try {
            log.info("c=AssignmentOrderUseCase m=execute s=Start orderId={} employeeId={}", orderId, employeeId);
            final var order = orderGateway.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("order.not.found"));

            final var employee = userGateway.findByUserId(employeeId);

            if (employee == null){
                throw new NotFoundException("user.not.found");
            }

            if (order.isOrderInInitialStatus()){
                throw new DomainException("order.with.a.different.from.the.initial.status");
            }

            order.assignEmployee(employee.getIdentifier());
            final var history = ApplicationOrderHistoryMapper.map(order);

            orderGateway.save(order);
            orderHistoryGateway.save(history);
            log.info("c=AssignmentOrderUseCase m=execute s=Done orderId={} employeeId={}", orderId, employeeId);
        } catch (Exception exception) {
            log.error("c=AssignmentOrderUseCase m=execute s=Error orderId={} employeeId={} message={}", orderId, employeeId, exception.getMessage());
            throw exception;
        }
    }
}
