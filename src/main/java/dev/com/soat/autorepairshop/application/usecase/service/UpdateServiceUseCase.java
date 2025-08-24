package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateServiceUseCase {

    private final ServiceGateway gateway;

    public void execute(Long idService, ServiceInputDTO service){
        final ServiceDomain existingService = gateway.findById(idService);

        if(existingService == null){
            throw new NotFoundException("Service not found.");
        }

        final ServiceDomain domain = ServiceApplicationMapper.toDomain(service);
        gateway.update(existingService, domain);
    }
}
