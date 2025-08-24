package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.ClientStatus;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.objects.Email;
import dev.com.soat.autorepairshop.domain.objects.Phone;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClientDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final String name;
    private final Document document;
    private final Phone phone;
    private final Email email;
    private final ClientStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ClientDomain(final Long identifier,
                         final String name,
                         final String document,
                         final String phone,
                         final String email,
                         final ClientStatus status,
                         final LocalDateTime createdAt,
                         final LocalDateTime updatedAt) {
        this.identifier = identifier;
        this.name = name;
        this.document = Document.from(document);
        this.phone = new Phone(phone);
        this.email = new Email(email);
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    @Override
    public Long getIdentifier() {
        return identifier;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUnformattedDocument() {
        return this.document.unformat();
    }

    public String getFormatedDocument() {
        return document.format();
    }

    public String getPhone() {
        return phone.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public boolean isDeleted() {
        return this.status.equals(ClientStatus.DELETED);
    }

}
