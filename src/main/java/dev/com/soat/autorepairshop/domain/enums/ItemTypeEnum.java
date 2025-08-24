package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

@Getter
public enum ItemTypeEnum {

    SERVICE("SERVICE", "Serviço"),
    PART("PART", "Peça/insumo");

    private final String name;

    private final String description;

    ItemTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static ItemTypeEnum fromName(String name) {
        if (name == null) {
            return null;
        }
        for (ItemTypeEnum type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ItemTypeEnum name: " + name);
    }
}
