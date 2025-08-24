-- =================================================================
-- Script de Migration Unificado
-- =================================================================

-- =================================================================
-- Tipos (ENUMs) - NÃO DEVERÁ SER UTILIZADO - SOMENTE PARA CONSULTA.
-- =================================================================
CREATE TYPE movement_type_enum AS ENUM ('INITIAL', 'INBOUND', 'OUTBOUND_SALE', 'ADJUSTMENT');
CREATE TYPE item_type_enum AS ENUM ('SERVICE', 'PART');
CREATE TYPE budget_status_enum AS ENUM ('PENDING_APPROVAL', 'APPROVED', 'REJECTED');
CREATE TYPE notification_channel_enum AS ENUM ('EMAIL', 'WHATSAPP', 'SMS');
CREATE TYPE notification_status_enum AS ENUM ('PENDING', 'FAILED', 'DELIVERED');
CREATE TYPE waiting_list_status_enum AS ENUM ('WAITING', 'CONTACTED', 'SCHEDULED', 'CANCELED');
CREATE TYPE client_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'DELETED');
CREATE TYPE users_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'DELETED');


-- =================================================================
-- Tabela: roles_tb
-- =================================================================
CREATE TABLE roles_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- =================================================================
-- Tabela: clients_tb
-- =================================================================
CREATE TABLE clients_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    document VARCHAR(18) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

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


-- =================================================================
-- Tabela: vehicles_tb
-- =================================================================
CREATE TABLE vehicles_tb (
    id SERIAL PRIMARY KEY,
    license_plate VARCHAR(8) UNIQUE NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    manufacture_year INTEGER NOT NULL,
    vehicle_type VARCHAR(5) NOT NULL,
    car_body_type VARCHAR(100),
    motorcycle_style_type VARCHAR(100),
    color VARCHAR(100) NOT NULL,
    document VARCHAR(18) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT uk_license_plate_owner UNIQUE (license_plate, document)
);


-- =================================================================
-- Tabela: services_tb
-- =================================================================
CREATE TABLE services_tb (
    id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    base_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


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
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


-- =================================================================
-- Tabela: part_inventory_tb
-- =================================================================
CREATE TABLE part_inventory_tb (
    part_id INTEGER PRIMARY KEY REFERENCES parts_tb(id) ON DELETE CASCADE,
    quantity_on_hand INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);


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

-- =================================================================
-- Tabela: budget_items_tb
-- =================================================================
CREATE TABLE budget_items_tb (
    id SERIAL PRIMARY KEY,
    budget_id INTEGER NOT NULL REFERENCES budgets_tb(id) ON DELETE CASCADE,
    item_type VARCHAR(50) NOT NULL,
    item_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


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
    updated_at TIMESTAMP
);


-- =================================================================
-- Tabela: part_inventory_movements_tb
-- =================================================================
CREATE TABLE part_inventory_movements_tb (
    id SERIAL PRIMARY KEY,
    part_id INTEGER NOT NULL,
    user_id INTEGER,
    service_order_id INTEGER,
    movement_type TEXT NOT NULL,
    quantity_changed INTEGER NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


-- =================================================================
-- Tabela: service_order_status_history_tb
-- =================================================================
CREATE TABLE service_order_status_history_tb (
    id SERIAL PRIMARY KEY,
    service_order_id INTEGER NOT NULL REFERENCES service_orders_tb(id) ON DELETE CASCADE,
    status VARCHAR(30) NOT NULL,
    notes TEXT,
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


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
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


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


-- =================================================================
-- Inserção de Dados (Seed)
-- =================================================================
INSERT INTO roles_tb (id, name) VALUES (1, 'ADMIN');
INSERT INTO roles_tb (id, name) VALUES (2, 'MECHANICAL');
INSERT INTO roles_tb (id, name) VALUES (3, 'ASSISTANT');


-- =================================================================
-- Adição de Chaves Estrangeiras (Foreign Keys)
-- =================================================================

-- Chaves para a tabela: service_orders_tb
ALTER TABLE service_orders_tb
    ADD CONSTRAINT fk_active_budget FOREIGN KEY (active_budget_id) REFERENCES budgets_tb(id),
    ADD CONSTRAINT fk_client_document FOREIGN KEY (client_document) REFERENCES clients_tb(document),
    ADD CONSTRAINT fk_vehicle_license_plate FOREIGN KEY (vehicle_license_plate) REFERENCES vehicles_tb(license_plate);

-- Chaves para a tabela: budgets_tb
ALTER TABLE budgets_tb
    ADD CONSTRAINT fk_service_order FOREIGN KEY (service_order_id) REFERENCES service_orders_tb(id) ON DELETE CASCADE;

-- Chaves para a tabela: part_inventory_movements_tb
ALTER TABLE part_inventory_movements_tb
    ADD CONSTRAINT fk_part FOREIGN KEY (part_id) REFERENCES parts_tb(id),
    ADD CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_tb(id),
    ADD CONSTRAINT fk_service_order FOREIGN KEY (service_order_id) REFERENCES service_orders_tb(id);