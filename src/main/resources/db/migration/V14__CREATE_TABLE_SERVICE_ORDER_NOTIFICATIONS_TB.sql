-- =================================================================
-- Tabela: service_order_notifications_tb
-- =================================================================
CREATE TABLE service_order_notifications_tb (
    id SERIAL PRIMARY KEY,
    service_order_id INTEGER NOT NULL REFERENCES service_orders_tb(id) ON DELETE CASCADE,
    client_id INTEGER NOT NULL REFERENCES clients_tb(id),
    channel TEXT NOT NULL,
    message_content TEXT,
    status TEXT NOT NULL,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);