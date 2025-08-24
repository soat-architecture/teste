package dev.com.soat.autorepairshop.domain.enums;

import java.util.Arrays;

public enum BasicRoleType {
    ADMIN,
    MECHANICAL,
    ASSISTANT;

    public static BasicRoleType getBasicRoleOrNull(String role){
        return Arrays.stream(values()).filter(it -> it.name().equals(role))
                .findFirst().orElse(null);
    }
}
