package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderMapper;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetOrdersUseCase {

    private final OrderGateway gateway;

    public List<OrderOutputDTO> execute(String status, Long employeeId){
        List<OrderDomain> allOrders = gateway.findAllOrders();

        if (status != null){
            OrderStatusType orderStatusType = OrderStatusType.fromName(status);
            if (orderStatusType != null)
                allOrders = allOrders.stream().filter(orderDomain -> orderDomain.getStatus().equals(orderStatusType)).toList();
        }

        if (employeeId != null)
            allOrders = allOrders.stream().filter(orderDomain -> orderDomain.getEmployeeId().equals(employeeId)).toList();

        List<OrderOutputDTO> result = allOrders
                .stream()
                .map(ApplicationOrderMapper::toDTO)
                .toList();

        if(result.isEmpty()){
            throw new NotFoundException("No orders found");
        }

        return result;
    }
}
