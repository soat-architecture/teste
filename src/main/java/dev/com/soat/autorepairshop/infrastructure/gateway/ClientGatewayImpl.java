package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.infrastructure.api.mapper.ClientMapper;
import dev.com.soat.autorepairshop.infrastructure.repository.ClientRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.EntityClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientGatewayImpl implements ClientGateway {

    private final ClientRepository repository;

    @Transactional
    @Override
    public ClientDomain save(final ClientDomain client) {
        var entity = EntityClientMapper.map(null, client);
        entity = repository.save(entity);
        return ClientMapper.map(entity);
    }

    @Transactional
    @Override
    public ClientDomain update(ClientDomain existingUser, ClientDomain newData) {

        var entity = EntityClientMapper.map(existingUser.getIdentifier(), newData);
        entity.setCreatedAt(existingUser.getCreatedAt());

        entity = repository.save(entity);

        return ClientMapper.map(entity);
    }

    @Override
    public ClientDomain findByDocument(final String doc) {
        var document = Document.from(doc);
        var client = this.repository.findByDocument(document.unformat());
        return ClientMapper.map(client);
    }

    @Override
    public ClientDomain findByConstraints(String document, String email, String phone) {
        var client = this.repository.findByConstraints(Document.cleanDocument(document), email, phone);
        return ClientMapper.map(client);
    }

    public Optional<ClientDomain> findById(final Long id) {
        var client = this.repository.findById(id);
        return client.map(ClientMapper::map);
    }

    @Transactional
    @Override
    public void delete(String document) {
        document = Document.cleanDocument(document);
        this.repository.softDeleteByDocument(document);
    }

}
