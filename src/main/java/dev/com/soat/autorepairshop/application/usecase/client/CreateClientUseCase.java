package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateClientUseCase {

    private final ClientGateway gateway;

    public void execute(ClientInputDTO client) {
        log.info("c=CreateClientUseCase m=execute s=start document={}", client.maskedDocument());

        final var existingUser = gateway.findByConstraints(client.unformattedDocument(), client.email(), client.phone());

        if (existingUser != null) {
            throw new DomainException("client.already.exists");
        }

        final var domain = ClientMapper.map(client);

        gateway.save(domain);

    }
}
