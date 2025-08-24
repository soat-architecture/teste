-- =================================================================
-- Tabela: budget_items_tb
-- =================================================================
CREATE TABLE budget_items_tb (
    id SERIAL PRIMARY KEY,
    budget_id INTEGER NOT NULL REFERENCES budgets_tb(id) ON DELETE CASCADE,
    item_type VARCHAR(50) NOT NULL,
    service_id INT,
    part_id INT,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);