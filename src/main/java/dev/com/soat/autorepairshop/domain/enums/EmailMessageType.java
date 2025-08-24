package dev.com.soat.autorepairshop.domain.enums;

import lombok.Getter;

@Getter
public enum EmailMessageType {
    BUDGET_EMAIL_TITLE("Orçamento - Auto Repair Shop", "Orçamento Mecânica - Auto Repair Shop!", true);

    private final String channel = "EMAIL";
    private final String title;
    private final String subject;
    private final Boolean isHtml;

    EmailMessageType(final String title, final String subject, final Boolean isHtml) {
        this.title = title;
        this.subject = subject;
        this.isHtml = isHtml;
    }
}
