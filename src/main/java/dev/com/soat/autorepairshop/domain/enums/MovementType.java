package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

@Getter
public enum MovementType {

    INITIAL("Initial"),
    INBOUND("Inbound"),
    OUTBOUND_SALE("Outbound sale"),
    ADJUSTMENT("Adjustment");

    private final String name;

    MovementType(String name) { this.name = name; }

    public static MovementType fromName(String name) {
        for(MovementType type : MovementType.values()) {
            if(type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }
}
