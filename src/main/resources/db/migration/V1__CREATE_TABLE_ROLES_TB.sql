-- =================================================================
-- Tabela: roles_tb
-- =================================================================
CREATE TABLE roles_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);
