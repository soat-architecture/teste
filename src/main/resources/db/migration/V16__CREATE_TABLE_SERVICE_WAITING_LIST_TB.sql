-- =================================================================
-- Tabela: service_waiting_list_tb
-- =================================================================
CREATE TABLE service_waiting_list_tb (
    id SERIAL PRIMARY KEY,
    client_id INTEGER NOT NULL REFERENCES clients_tb(id),
    vehicle_id INTEGER NOT NULL REFERENCES vehicles_tb(id),
    service_id INTEGER NOT NULL REFERENCES services_tb(id),
    status TEXT NOT NULL DEFAULT 'WAITING',
    notes TEXT,
    requested_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);