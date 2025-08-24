-- =================================================================
-- Tabela: service_metrics_tb
-- =================================================================
CREATE TABLE service_metrics_tb (
    service_id INTEGER PRIMARY KEY REFERENCES services_tb(id) ON DELETE CASCADE,
    completed_count INTEGER NOT NULL DEFAULT 0,
    total_execution_minutes BIGINT NOT NULL DEFAULT 0,
    average_execution_minutes INTEGER NOT NULL DEFAULT 0,
    last_updated_at TIMESTAMP WITH TIME ZONE
);