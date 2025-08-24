-- =================================================================
-- Tabela: service_order_status_history_tb
-- =================================================================
CREATE TABLE service_order_status_history_tb (
    id SERIAL PRIMARY KEY,
    service_order_id INTEGER NOT NULL REFERENCES service_orders_tb(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);