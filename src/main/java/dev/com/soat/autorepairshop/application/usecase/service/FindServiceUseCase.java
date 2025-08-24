package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.application.mapper.ServiceApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.ServiceOutputDTO;
import dev.com.soat.autorepairshop.domain.entity.ServiceDomain;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindServiceUseCase {

    private final ServiceGateway gateway;

    public ServiceOutputDTO execute(Long id){
        log.info("c=FindServiceUseCase m=execute s=start id={}", id);

        ServiceDomain domain = gateway.findById(id);

        if(domain == null){
            throw new NotFoundException("Service not found.");
        }

        return ServiceApplicationMapper.toDTO(domain);
    }
}
