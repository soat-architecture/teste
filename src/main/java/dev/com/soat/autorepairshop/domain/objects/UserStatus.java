package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.enums.UserStatusType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class UserStatus extends ValueObject<String> {

    public UserStatus(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public static UserStatus fromDomain(){
        return new UserStatus(UserStatusType.ACTIVE.getName());
    }

    @Override
    protected void validate(String value) {
        var existingRole = UserStatusType.getTypeOrNull(value);
        if (existingRole == null) throw new DomainException("");
    }
}
