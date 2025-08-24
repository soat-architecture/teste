package dev.com.soat.autorepairshop.infrastructure.adapter;

import dev.com.soat.autorepairshop.domain.entity.BudgetAggregateRoot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TemplateEmailAdapter {
    @Value("${server.port}")
    private String serverPort;

    private static final String CONST_STYLE_GRID = "<td style=\"padding: 10px; border: 1px solid #ccc;\">";
    private static final String CONST_STYLE_GRID_AMOUNT = "<td style=\"padding: 10px; border: 1px solid #ccc;\">R$ ";
    private static final String CONST_STYLE_GRID_TD = "</td>";

    public String buildBudgetMessage(final String title,
                                     final BudgetAggregateRoot budgetAggregateRoot){
        final StringBuilder html = new StringBuilder();

        html.append(String.format("""
        <html>
        <head>
            <meta charset="UTF-8">
            <title>%s</title>
        </head>
        <body style="font-family: Arial, sans-serif; color: #333; background-color: #f7f7f7; padding: 20px;">
            <div style="max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <h2 style="text-align: center; color: #2c3e50;">Auto Repair Shop 12SOAT</h2>
                <p>Olá,</p>
                <p>Segue abaixo o orçamento solicitado:</p>

                <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                    <thead>
                        <tr style="background-color: #f0f0f0;">
                            <th style="padding: 10px; border: 1px solid #ccc;">Nome</th>
                            <th style="padding: 10px; border: 1px solid #ccc;">Tipo</th>
                            <th style="padding: 10px; border: 1px solid #ccc;">Quantidade</th>
                            <th style="padding: 10px; border: 1px solid #ccc;">Valor Unitário</th>
                        </tr>
                    </thead>
                    <tbody>
        """, title));

        budgetAggregateRoot.getBudgetItem().forEach(item -> {
            if (item.getService() != null){
                html.append("<tr>")
                        .append(CONST_STYLE_GRID).append(item.getService().getName()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID).append(item.getService().getDescription()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID).append(item.getBudgetItem().getQuantity()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID_AMOUNT).append(String.format("%.2f", item.getBudgetItem().getUnitPrice())).append(CONST_STYLE_GRID_TD)
                        .append("</tr>");
            } else if (item.getPart() != null){
                html.append("<tr>")
                        .append(CONST_STYLE_GRID).append(item.getPart().getName()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID).append(item.getPart().getDescription()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID).append(item.getBudgetItem().getQuantity()).append(CONST_STYLE_GRID_TD)
                        .append(CONST_STYLE_GRID_AMOUNT).append(String.format("%.2f", item.getBudgetItem().getUnitPrice())).append(CONST_STYLE_GRID_TD)
                        .append("</tr>");
            }
        });

        html.append("""
                    </tbody>
                </table>

                <p style="text-align: right; font-size: 18px; font-weight: bold;">
                    Valor Total: R$ %s
                </p>
        """.formatted(String.format("%.2f", budgetAggregateRoot.getBudget().getTotalAmount())));

        html.append("<div style=\"text-align:center; margin-top:20px;\">");

        html.append("<a href=\"http://localhost:")
                .append(serverPort)
                .append("/api/v1/budgets/approve/")
                .append(budgetAggregateRoot.getBudget().getServiceOrderId())
                .append("\" target=\"_blank\" ")
                .append("style=\"display:inline-block; padding:12px 24px; background-color:#27ae60; color:white; text-decoration:none; border-radius:5px; font-size:16px; font-weight:bold;\">")
                .append("APROVAR ORÇAMENTO")
                .append("</a>");

        html.append("&nbsp;&nbsp;");

        html.append("<a href=\"http://localhost:")
                .append(serverPort)
                .append("/api/v1/budgets/not-approve/")
                .append(budgetAggregateRoot.getBudget().getServiceOrderId())
                .append("\" target=\"_blank\" ")
                .append("style=\"display:inline-block; padding:12px 24px; background-color:#c0392b; color:white; text-decoration:none; border-radius:5px; font-size:16px; font-weight:bold;\">")
                .append("REJEITAR ORÇAMENTO")
                .append("</a>");

        html.append("</div>");

        html.append("""
                <p style="margin-top: 30px; font-size: 12px; color: #888;">Se você não solicitou este orçamento, por favor ignore este e-mail.</p>
            </div>
        </body>
        </html>
        """);

        return html.toString();
    }
}
