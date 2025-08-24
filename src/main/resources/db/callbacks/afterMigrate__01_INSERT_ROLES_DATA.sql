DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM roles_tb WHERE id = 1) THEN
            INSERT INTO roles_tb (id, name) VALUES (1, 'ADMIN');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM roles_tb WHERE id = 2) THEN
            INSERT INTO roles_tb (id, name) VALUES (2, 'MECHANICAL');
        END IF;

        IF NOT EXISTS (SELECT 1 FROM roles_tb WHERE id = 3) THEN
            INSERT INTO roles_tb (id, name) VALUES (3, 'ASSISTANT');
        END IF;
    END
$$;
