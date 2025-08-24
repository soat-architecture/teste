package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class Phone extends ValueObject<String> {

    public static final String DEFAULT_PHONE_REGEX = "^\\([1-9]{2}\\) (?:9\\d|[2-9])\\d{3}-\\d{4}$";

    public Phone(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        if (value.isEmpty()) {
            throw new DomainException("phone.cannot.be.null");
        }

        if (!value.matches(DEFAULT_PHONE_REGEX)) {
            throw new DomainException("phone.is.not.valid");
        }
    }
}
