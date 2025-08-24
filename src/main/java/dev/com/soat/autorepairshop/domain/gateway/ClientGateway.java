package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.ClientDomain;

import java.util.Optional;

public interface ClientGateway {
    ClientDomain save(final ClientDomain client);
    ClientDomain update(ClientDomain existingUser, ClientDomain newData);
    ClientDomain findByDocument(final String document);
    ClientDomain findByConstraints(final String document, final String email, final String phone);
    void delete(final String document);
}
