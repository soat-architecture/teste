-- =================================================================
-- Tabela: users_tb
-- =================================================================
CREATE TABLE users_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    document VARCHAR(18) NOT NULL UNIQUE,
    contracted_at TIMESTAMP NOT NULL,
    role_id INTEGER NOT NULL REFERENCES roles_tb(id),
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP
);