-- =================================================================
-- Tabela: service_orders_tb
-- =================================================================
CREATE TABLE service_orders_tb (
    id SERIAL PRIMARY KEY,
    code VARCHAR(30),
    client_document VARCHAR(255),
    vehicle_license_plate VARCHAR(32),
    status VARCHAR(30) DEFAULT 'RECEBIDA',
    active_budget_id INTEGER,
    employee_id BIGINT NOT NULL,
    service_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);
