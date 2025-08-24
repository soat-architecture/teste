package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClientStatus {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    DELETED("DELETED");

    private final String name;

    public static ClientStatus fromName(String name) {
        for (ClientStatus clientStatus : ClientStatus.values()) {
            if (clientStatus.getName().equals(name)) {
                return clientStatus;
            }
        }
        return null;
    }

}
