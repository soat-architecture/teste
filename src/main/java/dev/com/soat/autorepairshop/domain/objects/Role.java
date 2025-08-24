package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.enums.BasicRoleType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class Role extends ValueObject<String> {

    public Role(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        var existingRole = BasicRoleType.getBasicRoleOrNull(value);
        if (existingRole == null) throw new DomainException("generic.error");
    }
}
