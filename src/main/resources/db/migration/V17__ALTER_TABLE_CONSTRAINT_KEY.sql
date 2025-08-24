-- =================================================================
-- Adição de Chaves Estrangeiras (Foreign Keys)
-- =================================================================

-- Chaves para a tabela: service_orders_tb
ALTER TABLE service_orders_tb
    ADD CONSTRAINT fk_active_budget FOREIGN KEY (active_budget_id) REFERENCES budgets_tb(id),
    ADD CONSTRAINT fk_client_document FOREIGN KEY (client_document) REFERENCES clients_tb(document),
    ADD CONSTRAINT fk_vehicle_license_plate FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles_tb(license_plate);

-- Chaves para a tabela: budgets_tb
ALTER TABLE budgets_tb
    ADD CONSTRAINT fk_service_order FOREIGN KEY (service_order_id) REFERENCES service_orders_tb(id) ON DELETE CASCADE;

-- Chaves para a tabela: budget_items_tb
ALTER TABLE budget_items_tb
    ADD CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES services_tb(id),
    ADD CONSTRAINT fk_part FOREIGN KEY (part_id) REFERENCES parts_tb(id);

-- Chaves para a tabela: part_inventory_movements_tb
ALTER TABLE part_inventory_movements_tb
    ADD CONSTRAINT fk_part FOREIGN KEY (part_id) REFERENCES parts_tb(id),
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_tb(id),
    ADD CONSTRAINT fk_service_order FOREIGN KEY (service_order_id) REFERENCES service_orders_tb(id);