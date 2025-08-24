package dev.com.soat.autorepairshop.application.models.input;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.shared.masker.DocumentMasker;

public record ClientInputDTO(
    Long identifier,
    String name,
    String document,
    String phone,
    String email,
    ClientStatus status
) {
    public String maskedDocument() {
        return DocumentMasker.mask(document);
    }
    public String unformattedDocument() {
        return Document.from(document).unformat();
    }
}