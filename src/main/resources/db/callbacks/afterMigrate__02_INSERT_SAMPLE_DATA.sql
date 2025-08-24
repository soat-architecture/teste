DO $$
    BEGIN
        -- Insert sample clients
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.clients_tb WHERE document = '25392659012'
        ) THEN
            INSERT INTO autorepairshop.clients_tb ("name", "document", phone, email) VALUES
                ('João Silva', '25392659012', '(11) 99999-9991', 'joao.silva@example.com');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.clients_tb WHERE document = '22239623004'
        ) THEN
            INSERT INTO autorepairshop.clients_tb ("name", "document", phone, email) VALUES
                ('Maria Oliveira', '22239623004', '(11) 99999-9992', 'maria.oliveira@example.com');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.clients_tb WHERE document = '11695761090'
        ) THEN
            INSERT INTO autorepairshop.clients_tb ("name", "document", phone, email) VALUES
                ('Carlos Pereira', '11695761090', '(11) 99999-9993', 'carlos.pereira@example.com');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.clients_tb WHERE document = '74722983011'
        ) THEN
            INSERT INTO autorepairshop.clients_tb ("name", "document", phone, email) VALUES
                ('Ana Costa', '74722983011', '(11) 99999-9994', 'ana.costa@example.com');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.clients_tb WHERE document = '07701493085'
        ) THEN
            INSERT INTO autorepairshop.clients_tb ("name", "document", phone, email) VALUES
                ('Pedro Santos', '07701493085', '(11) 99999-9995', 'pedro.santos@example.com');
        END IF;

        -- Insert sample vehicles
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.vehicles_tb WHERE license_plate = 'BRA2E19'
        ) THEN
            INSERT INTO autorepairshop.vehicles_tb (license_plate, brand, model, manufacture_year, vehicle_type, car_body_type, motorcycle_style_type, color, "document") VALUES
                ('BRA2E19', 'Toyota', 'Corolla', 2022, 'CARRO', 'SEDAN', NULL, 'Preto', '25392659012');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.vehicles_tb WHERE license_plate = 'MERC052'
        ) THEN
            INSERT INTO autorepairshop.vehicles_tb (license_plate, brand, model, manufacture_year, vehicle_type, car_body_type, motorcycle_style_type, color, "document") VALUES
                ('MERC052', 'Honda', 'Civic', 2021, 'CARRO', 'SEDAN', NULL, 'Branco', '22239623004');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.vehicles_tb WHERE license_plate = 'ARG3A45'
        ) THEN
            INSERT INTO autorepairshop.vehicles_tb (license_plate, brand, model, manufacture_year, vehicle_type, car_body_type, motorcycle_style_type, color, "document") VALUES
                ('ARG3A45', 'Yamaha', 'MT-07', 2023, 'MOTO', NULL, 'NAKED', 'Azul', '11695761090');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.vehicles_tb WHERE license_plate = 'URU4B58'
        ) THEN
            INSERT INTO autorepairshop.vehicles_tb (license_plate, brand, model, manufacture_year, vehicle_type, car_body_type, motorcycle_style_type, color, "document") VALUES
                ('URU4B58', 'Volkswagen', 'Golf', 2020, 'CARRO', 'HATCH', NULL, 'Vermelho', '74722983011');
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.vehicles_tb WHERE license_plate = 'PAR1G89'
        ) THEN
            INSERT INTO autorepairshop.vehicles_tb (license_plate, brand, model, manufacture_year, vehicle_type, car_body_type, motorcycle_style_type, color, "document") VALUES
                ('PAR1G89', 'Honda', 'CB 500F', 2022, 'MOTO', NULL, 'NAKED', 'Cinza', '07701493085');
        END IF;

        -- Insert sample services
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.services_tb WHERE name = 'Troca de Óleo'
        ) THEN
            INSERT INTO autorepairshop.services_tb (name, description, base_price) VALUES
                ('Troca de Óleo', 'Serviço de troca de óleo do motor e filtro.', 150.00);
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.services_tb WHERE name = 'Alinhamento e Balanceamento'
        ) THEN
            INSERT INTO autorepairshop.services_tb (name, description, base_price) VALUES
                ('Alinhamento e Balanceamento', 'Serviço de alinhamento da direção e balanceamento das rodas.', 120.00);
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.services_tb WHERE name = 'Revisão Geral'
        ) THEN
            INSERT INTO autorepairshop.services_tb (name, description, base_price) VALUES
                ('Revisão Geral', 'Verificação completa de itens de segurança e funcionamento do veículo.', 300.00);
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.services_tb WHERE name = 'Troca de Pneus'
        ) THEN
            INSERT INTO autorepairshop.services_tb (name, description, base_price) VALUES
                ('Troca de Pneus', 'Substituição de pneus e calibragem.', 80.00);
        END IF;
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.services_tb WHERE name = 'Inspeção de Freios'
        ) THEN
            INSERT INTO autorepairshop.services_tb (name, description, base_price) VALUES
                ('Inspeção de Freios', 'Verificação e ajuste do sistema de freios.', 90.00);
        END IF;

        -- Insert sample part
        -- Pastilha de Freio
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.parts_tb WHERE sku = 'PAST-FREIO-123'
        ) THEN
            INSERT INTO autorepairshop.parts_tb (name, sku, description, brand, selling_price, buy_price)
            VALUES ('Pastilha de Freio', 'PAST-FREIO-123', 'Pastilha de freio para veículos de passeio', 'Bosch', 150.00, 100.00);
        END IF;

        -- Filtro de Óleo
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.parts_tb WHERE sku = 'FILT-OLEO-456'
        ) THEN
            INSERT INTO autorepairshop.parts_tb (name, sku, description, brand, selling_price, buy_price)
            VALUES ('Filtro de Óleo', 'FILT-OLEO-456', 'Filtro de óleo para motores a combustão', 'Fram', 40.00, 25.00);
        END IF;

        -- Vela de Ignição
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.parts_tb WHERE sku = 'VELA-IGN-789'
        ) THEN
            INSERT INTO autorepairshop.parts_tb (name, sku, description, brand, selling_price, buy_price)
            VALUES ('Vela de Ignição', 'VELA-IGN-789', 'Vela de ignição para motores a gasolina/flex', 'NGK', 35.00, 20.00);
        END IF;

        -- Fluido de Freio DOT 4
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.parts_tb WHERE sku = 'FLUI-DOT4-321'
        ) THEN
            INSERT INTO autorepairshop.parts_tb (name, sku, description, brand, selling_price, buy_price)
            VALUES ('Fluido de Freio DOT 4', 'FLUI-DOT4-321', 'Fluido de freio DOT 4 para sistemas hidráulicos', 'Varga', 30.00, 18.00);
        END IF;

        -- Correia Dentada
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.parts_tb WHERE sku = 'CORR-DENT-654'
        ) THEN
            INSERT INTO autorepairshop.parts_tb (name, sku, description, brand, selling_price, buy_price)
            VALUES ('Correia Dentada', 'CORR-DENT-654', 'Correia dentada para sincronismo do motor', 'Gates', 120.00, 80.00);
        END IF;

        -- Insert sample part
        -- Pastilha de Freio
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.part_inventory_tb
            WHERE part_id = (SELECT id FROM autorepairshop.parts_tb WHERE sku = 'PAST-FREIO-123')
        ) THEN
            INSERT INTO autorepairshop.part_inventory_tb (part_id, quantity_on_hand)
            VALUES ((SELECT id FROM autorepairshop.parts_tb WHERE sku = 'PAST-FREIO-123'), 20);
        END IF;

        -- Filtro de Óleo
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.part_inventory_tb
            WHERE part_id = (SELECT id FROM autorepairshop.parts_tb WHERE sku = 'FILT-OLEO-456')
        ) THEN
            INSERT INTO autorepairshop.part_inventory_tb (part_id, quantity_on_hand)
            VALUES ((SELECT id FROM autorepairshop.parts_tb WHERE sku = 'FILT-OLEO-456'), 50);
        END IF;

        -- Vela de Ignição
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.part_inventory_tb
            WHERE part_id = (SELECT id FROM autorepairshop.parts_tb WHERE sku = 'VELA-IGN-789')
        ) THEN
            INSERT INTO autorepairshop.part_inventory_tb (part_id, quantity_on_hand)
            VALUES ((SELECT id FROM autorepairshop.parts_tb WHERE sku = 'VELA-IGN-789'), 100);
        END IF;

        -- Fluido de Freio DOT 4
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.part_inventory_tb
            WHERE part_id = (SELECT id FROM autorepairshop.parts_tb WHERE sku = 'FLUI-DOT4-321')
        ) THEN
            INSERT INTO autorepairshop.part_inventory_tb (part_id, quantity_on_hand)
            VALUES ((SELECT id FROM autorepairshop.parts_tb WHERE sku = 'FLUI-DOT4-321'), 30);
        END IF;

        -- Correia Dentada
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.part_inventory_tb
            WHERE part_id = (SELECT id FROM autorepairshop.parts_tb WHERE sku = 'CORR-DENT-654')
        ) THEN
            INSERT INTO autorepairshop.part_inventory_tb (part_id, quantity_on_hand)
            VALUES ((SELECT id FROM autorepairshop.parts_tb WHERE sku = 'CORR-DENT-654'), 15);
        END IF;
    END
$$;