package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.gateway.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModifierEmployeeOrderUseCase {
    private final UserGateway userGateway;
    private final OrderGateway orderGateway;

    public void execute(final Long orderId, final Long newEmployeeId){
        try {
            log.info("c=ModifierEmployeeOrderUseCase m=execute s=Start orderId={} newEmployeeId={}", orderId, newEmployeeId);
            final var order = orderGateway.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("order.not.found"));

            final var employee = userGateway.findByUserId(newEmployeeId);

            if (employee == null){
                throw new NotFoundException("user.not.found");
            }

            order.updateEmployee(newEmployeeId);
            orderGateway.save(order);
            log.info("c=ModifierEmployeeOrderUseCase m=execute s=Done orderId={} newEmployeeId={}", orderId, newEmployeeId);
        } catch (Exception exception) {
            log.error("c=ModifierEmployeeOrderUseCase m=execute s=Error orderId={} newEmployeeId={} message={}", orderId, newEmployeeId, exception.getMessage());
            throw exception;
        }
    }
}
