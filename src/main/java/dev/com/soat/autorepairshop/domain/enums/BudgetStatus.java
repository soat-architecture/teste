package dev.com.soat.autorepairshop.domain.enums;


import lombok.Getter;

@Getter
public enum BudgetStatus {

    PENDING_APPROVAL("PENDING_APPROVAL", "Pendente de aprovação"),
    APPROVED("APPROVED", "Aprovado"),
    REJECTED("REJECTED", "Rejeitado");

    private final String name;

    private final String description;

    BudgetStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name;
    }


    public static BudgetStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        for (BudgetStatus status : values()) {
            if (status.name.equalsIgnoreCase(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid BudgetStatus name: " + name);
    }
}