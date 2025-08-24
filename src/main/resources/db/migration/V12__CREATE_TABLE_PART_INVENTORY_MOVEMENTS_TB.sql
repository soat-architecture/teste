-- =================================================================
-- Tabela: part_inventory_movements_tb
-- =================================================================
CREATE TABLE part_inventory_movements_tb (
    id SERIAL PRIMARY KEY,
    part_id INTEGER NOT NULL,
    user_id INTEGER,
    service_order_id INTEGER,
    movement_type TEXT NOT NULL,
    quantity_changed INTEGER NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
