-- =================================================================
-- Tabela: part_inventory_tb
-- =================================================================
CREATE TABLE part_inventory_tb (
    part_id INTEGER PRIMARY KEY REFERENCES parts_tb(id) ON DELETE CASCADE,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);