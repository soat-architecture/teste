package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.usecase.order.dto.AverageServiceExecutionTimeDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalculateAverageServiceExecutionTimeUseCase {

    private final OrderGateway orderGateway;

    public AverageServiceExecutionTimeDTO execute() {
        List<OrderDomain> completedOrders = orderGateway.findAllByStatus(OrderStatusType.returnFinishedStatus());

        if (completedOrders.isEmpty()) {
            return AverageServiceExecutionTimeDTO.empty();
        }

        List<OrderDomain> validOrders = completedOrders.stream()
                .filter(order -> order.getCompletedAt().isAfter(order.getCreatedAt()))
                .toList();

        if (validOrders.isEmpty()) {
            log.warn("As ordens de serviço finalizadas não possuem datas de criação ou conclusão válidas.");
            return AverageServiceExecutionTimeDTO.empty();
        }

        long totalDurationSeconds = validOrders.stream()
                .mapToLong(order -> Duration.between(order.getCreatedAt(), order.getCompletedAt()).getSeconds())
                .sum();

        double avgSeconds = (double) totalDurationSeconds / validOrders.size();
        double avgMinutes = avgSeconds / 60.0;
        double avgHours = avgSeconds / 3600.0;

        long totalSeconds = (long) avgSeconds;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        String formattedTime = String.format("%d:%02d:%02d", hours, minutes, seconds);

        return new AverageServiceExecutionTimeDTO(avgSeconds, avgMinutes, avgHours, formattedTime);
    }
}
