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