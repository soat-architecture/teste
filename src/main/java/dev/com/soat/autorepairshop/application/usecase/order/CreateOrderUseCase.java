package dev.com.soat.autorepairshop.application.usecase.order;

import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderHistoryMapper;
import dev.com.soat.autorepairshop.application.mapper.ApplicationOrderMapper;
import dev.com.soat.autorepairshop.application.models.input.OrderInputDTO;
import dev.com.soat.autorepairshop.application.models.output.OrderOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.OrderValidationUtils;
import dev.com.soat.autorepairshop.application.utils.UserValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.OrderDomain;
import dev.com.soat.autorepairshop.domain.gateway.OrderGateway;
import dev.com.soat.autorepairshop.domain.entity.OrderHistoryDomain;
import dev.com.soat.autorepairshop.domain.gateway.*;
import dev.com.soat.autorepairshop.shared.masker.DocumentMasker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderGateway orderGateway;
    private final OrderHistoryGateway orderHistoryGateway;

    private final ClientValidationUtils clientValidationUtils;
    private final VehicleValidationUtils vehicleValidationUtils;
    private final UserValidationUtils userValidationUtils;
    private final OrderValidationUtils orderValidationUtils;

    @Transactional
    public OrderOutputDTO execute(OrderInputDTO order) {
        log.info("c=CreateOrderUseCase m=execute s=start client={} vehicle={} employee={}",
            DocumentMasker.mask(order.clientDocument()), order.vehicleLicensePlate(), order.employeeIdentifier()
        );

        clientValidationUtils.validateClientExistenceByDocument(order.clientDocument());
        userValidationUtils.validateUserExistenceById(order.employeeIdentifier());
        vehicleValidationUtils.validateVehicleExistenceByLicensePlate(order.vehicleLicensePlate());
        orderValidationUtils.validateActiveOrderByVehicleLicensePlate(order.vehicleLicensePlate());
        final var domain = ApplicationOrderMapper.toDomain(order);
        OrderDomain saved = orderGateway.save(domain);
        OrderHistoryDomain orderHistoryDomain = ApplicationOrderHistoryMapper.map(saved);
        orderHistoryGateway.save(orderHistoryDomain);
        return ApplicationOrderMapper.toDTO(saved);
    }

}
