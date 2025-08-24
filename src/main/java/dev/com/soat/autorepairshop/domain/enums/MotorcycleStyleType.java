package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

@Getter
public enum MotorcycleStyleType {
    STREET("Street"),
    ESPORTIVA("Esportiva"),
    CUSTOM("Custom"),
    TOURING("Touring"),
    BIG_TRAIL("Big Trail"),
    TRAIL("Trail"),
    SUPERMOTO("Supermoto"),
    SCOOTER("Scooter"),
    CICLOMOTOR("Ciclomotor"),
    CAFE_RACER("Café Racer"),
    BOBBER("Bobber"),
    CHOPPER("Chopper"),
    TRICICLO("Triciclo");

    private final String name;

    MotorcycleStyleType(String name) {
        this.name = name;
    }

    public static MotorcycleStyleType fromName(String name) {
        for (MotorcycleStyleType type : MotorcycleStyleType.values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
