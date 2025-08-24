package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryMovementDomain;

import java.util.List;

public interface InventoryMovementGateway {

    void save(InventoryMovementDomain domain);
    List<InventoryMovementDomain> findByPartId(final Long partId);
}
