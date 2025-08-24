-- =================================================================
-- Tabela: budgets_tb
-- =================================================================
CREATE TABLE budgets_tb (
    id SERIAL PRIMARY KEY,
    service_order_id INTEGER NOT NULL, -- FK adicionada ao final
    version INTEGER NOT NULL,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    evaluated_at TIMESTAMP WITH TIME ZONE
);