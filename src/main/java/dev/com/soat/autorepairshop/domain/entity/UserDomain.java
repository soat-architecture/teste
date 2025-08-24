package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.objects.Email;
import dev.com.soat.autorepairshop.domain.objects.Role;
import dev.com.soat.autorepairshop.domain.objects.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserDomain extends DomainEntity<Long> {

    private final Long identifier;
    private final String name;
    private final Document document;
    private final Email email;
    private final String password;
    private final LocalDateTime contractedAt;
    private Role role;
    private UserStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserDomain(final Long identifier,
                       final String name,
                       final String document,
                       final String email,
                       final String password,
                       final LocalDateTime contractedAt,
                       final String role,
                       final String status,
                       final LocalDateTime createdAt,
                       final LocalDateTime updatedAt) {
        this.identifier = identifier;
        this.name = name;
        this.document = Document.from(document);
        this.email = new Email(email);
        this.password = password;
        this.contractedAt = contractedAt;
        this.role = new Role(role);
        this.status = new UserStatus(status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * The 'identifier' and 'updatedAt' attributes should not be filled at this moment.
     * The database will generate the 'identifier' attribute sequentially,
     * while 'updatedAt' will be generated when the domain object undergoes changes.
     */
    public static UserDomain create(final String document,
                                    final String name,
                                    final String email,
                                    final String password,
                                    final LocalDateTime contractedAt,
                                    final String role){
        var createdAt = LocalDateTime.now();
        var status = UserStatus.fromDomain();
        return new UserDomain(
                null,
                name,
                document,
                email,
                password,
                contractedAt,
                role,
                status.getValue(),
                createdAt,
                null
        );
    }

    public static UserDomain restore(final Long identifier,
                                     final String name,
                                     final String document,
                                     final String email,
                                     final String password,
                                     final LocalDateTime contractedAt,
                                     final String role,
                                     final String status,
                                     final LocalDateTime createdAt,
                                     final LocalDateTime updatedAt){
        return new UserDomain(
                identifier,
                name,
                document,
                email,
                password,
                contractedAt,
                role,
                status,
                createdAt,
                updatedAt
        );
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

    public String getDocument() {
        return document.getValue();
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getRole() {
        return role.getValue();
    }

    public String getStatus() {
        return status.getValue();
    }

    public UserStatusType getStatusType() {
        return UserStatusType.getTypeOrNull(status.getValue());
    }

    public void updateRole(final String role){
        this.role = new Role(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void delete(){
        var deleted = UserStatusType.DELETED.getName();
        this.status = new UserStatus(deleted);
        this.updatedAt = LocalDateTime.now();
    }
}