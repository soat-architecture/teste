package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.ServiceInputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateServiceUseCase {

    private final ServiceGateway serviceGateway;

    public void execute(ServiceInputDTO service){
        if(service == null){
            throw new DomainException("Service input data must be provided");
        }

        final ServiceDomain domain = ServiceApplicationMapper.toDomain(service);
        serviceGateway.save(domain);
    }
}