package dev.com.soat.autorepairshop.infrastructure.api.models.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.com.soat.autorepairshop.domain.entity.*;
import dev.com.soat.autorepairshop.domain.enums.OrderStatusType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailsResponseDTO(

        @Schema(description = "Identificador da OS.", example = "455432")
        Long orderId,

        @Schema(description = "Documento do cliente (CPF/CNPJ).", example = "012.234.567-89")
        String clientDocument,

        @Schema(description = "Placa do veículo.", example = "ABC-1234 ou ABC1D34")
        String vehicleLicensePlate,

        @Schema(description = "Status da OS.", example = "Recebida")
        OrderStatusType orderStatus,

        @Schema(description = "Anotações da OS.", example = "Trocar parafuso.")
        String orderNotes,

        @Schema(description = "Nome do empregado.", example = "John Doe")
        String employeeName,

        @Schema(description = "Data de criação da OS.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime createdAt,

        @Schema(description = "Data de atualização da OS.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime updatedAt,

        @Schema(description = "Data de conclusão da OS.", example = "2025/07/13 17:00")
        @JsonFormat(pattern = "yyyy/MM/dd HH:mm")
        LocalDateTime completedAt,

        @Schema(description = "Orçamento atual vinculado a OS")
        BudgetDomain budget,

        @Schema(description = "Descrição dos itens presentes no orçamento")
        List<BudgetItemDomain> budgetItems,

        @Schema(description = "Histórico de atualização da OS")
        List<OrderHistoryDomain> history
) {
}
