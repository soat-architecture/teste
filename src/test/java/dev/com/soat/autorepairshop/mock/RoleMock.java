package dev.com.soat.autorepairshop.mock;

import dev.com.soat.autorepairshop.infrastructure.repository.entity.RoleEntity;

public class RoleMock {
    public static final Long DEFAULT_IDENTIFIER = 1L;
    public static final String DEFAULT_ADMIN_NAME = "ADMIN";

    public static RoleEntity buildMockEntity(){
        return new RoleEntity(
                DEFAULT_IDENTIFIER,
                DEFAULT_ADMIN_NAME
        );
    }
}
