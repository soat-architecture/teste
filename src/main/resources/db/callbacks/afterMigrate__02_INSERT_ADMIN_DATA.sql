DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1 FROM autorepairshop.users_tb WHERE email = 'admin@admin.com'
        ) THEN
            INSERT INTO autorepairshop.users_tb (
                id, name, email, password, document, contracted_at, role_id, created_at, updated_at, status
            ) VALUES (
                         999999999,
                         'Admin',
                         'admin@admin.com',
                         '$2a$10$AVa8PaKLQIo0FTcdOa9IguY8JfcgSmrfceiwK8GwPaK6/DQKygy2i',
                         '28449687080',
                         NOW(),
                         1,
                         NOW(),
                         NOW(),
                         'ACTIVE'
                     );
        END IF;
    END
$$;
