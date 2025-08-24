package dev.com.soat.autorepairshop.domain.gateway;

import dev.com.soat.autorepairshop.domain.entity.InventoryDomain;

public interface InventoryGateway {
    InventoryDomain save(final InventoryDomain inventory);
    void saveNotReturns(final InventoryDomain inventory);
    InventoryDomain update(InventoryDomain existingInventory, InventoryDomain newData);
    InventoryDomain findById(final Long partId);
    void delete(final Long partId);
}
