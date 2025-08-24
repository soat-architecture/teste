package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum OrderStatusType {

    RECEBIDA("RECEBIDA", "Recebida"),
    EM_DIAGNOSTICO("EM_DIAGNOSTICO", "Em diagnóstico"),
    AGUARDANDO_APROVACAO("AGUARDANDO_APROVACAO", "Aguardando aprovação"),
    APROVADA("APROVADA", "Aprovada"),
    EM_EXECUCAO("EM_EXECUCAO", "Em execução"),
    FINALIZADA("FINALIZADA", "Finalizada"),
    REJEITADA("REJEITADA", "Rejeitada"),
    ENTREGUE("ENTREGUE", "Entregue");

    private final String name;
    private final String description;

    OrderStatusType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static OrderStatusType fromName(String name) {
        for (OrderStatusType orderStatusType : OrderStatusType.values()) {
            if (orderStatusType.getDescription().equals(name)) {
                return orderStatusType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static List<OrderStatusType> returnFinishedStatus(){
        return List.of(ENTREGUE, FINALIZADA);
    }
}
