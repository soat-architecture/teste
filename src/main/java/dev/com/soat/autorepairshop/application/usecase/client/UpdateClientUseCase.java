package dev.com.soat.autorepairshop.application.usecase.client;

import dev.com.soat.autorepairshop.application.models.input.ClientInputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClientGateway gateway;

    private final ClientValidationUtils clientValidationUtils;

    public void execute(String doc, ClientInputDTO client) {
        var document = Document.from(doc);

        log.info("c=UpdateClientUseCase m=execute s=start document={}", document.mask());

        ClientDomain existingUser = clientValidationUtils.validateClientExistenceByDocument(document.unformat());

        final var newData = ClientMapper.map(client);

        gateway.update(existingUser, newData);

    }

}
