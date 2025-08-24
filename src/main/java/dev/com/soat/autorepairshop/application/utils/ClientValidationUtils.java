package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;

import dev.com.soat.autorepairshop.domain.objects.Document;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClientValidationUtils {

    private final ClientGateway clientGateway;

    public ClientDomain validateClientExistenceByDocument(String doc) {
        var document = Document.from(doc).unformat();
        ClientDomain existingClient = clientGateway.findByDocument(document);

        if (existingClient == null) {
            throw new NotFoundException("client.does.not.exists");
        }

        if (existingClient.isDeleted()) {
            throw new DomainException("client.exists.but.deleted");
        }

        return existingClient;
    }

    public void validateNewClientByDocument(String doc) {
        var document = Document.from(doc).unformat();
        ClientDomain existingClient = clientGateway.findByDocument(document);

        if (existingClient != null) {
            throw new ConflictException("client.already.exists");
        }
    }

}
