-- =================================================================
-- Tabela: parts_tb
-- =================================================================
CREATE TABLE parts_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    sku VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    brand VARCHAR(100),
    selling_price NUMERIC(10, 2) NOT NULL,
    buy_price NUMERIC(10, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);
