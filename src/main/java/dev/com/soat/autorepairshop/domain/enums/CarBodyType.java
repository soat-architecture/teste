package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

@Getter
public enum CarBodyType {
    SEDAN("Sedan"),
    HATCH("Hatch"),
    PERUA("Perua"),
    SUV("SUV"),
    CUPE("Cupê"),
    CONVERSIVEL("Conversível"),
    PICAPE("Picape"),
    MINIVAN("Minivan"),
    FASTBACK("Fastback"),
    ROADSTER("Roadster"),
    CROSSOVER("Crossover"),
    LIFTBACK("Liftback");

    private final String name;

    CarBodyType(String name) {
        this.name = name;
    }

    public static CarBodyType fromName(String name) {
        for (CarBodyType carBodyType : CarBodyType.values()) {
            if (carBodyType.getName().equalsIgnoreCase(name)) {
                return carBodyType;
            }
        }
        return null;
    }
}
