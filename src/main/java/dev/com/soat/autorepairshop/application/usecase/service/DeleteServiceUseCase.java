package dev.com.soat.autorepairshop.application.usecase.service;

import dev.com.soat.autorepairshop.domain.gateway.ServiceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteServiceUseCase {

    private final ServiceGateway gateway;

    public void execute(Long id){
        log.info("c=DeleteServiceUseCase m=execute s=start id={}", id);
        gateway.delete(id);
        log.info("c=DeleteServiceUseCase m=execute s=done id={}", id);
    }
}
