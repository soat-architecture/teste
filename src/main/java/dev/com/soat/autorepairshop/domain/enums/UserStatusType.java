package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserStatusType {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    DELETED("DELETED");

    private final String name;

    public static UserStatusType getTypeOrNull(String role){
        return Arrays.stream(values()).filter(it -> it.name().equals(role))
                .findFirst().orElse(null);
    }
}
