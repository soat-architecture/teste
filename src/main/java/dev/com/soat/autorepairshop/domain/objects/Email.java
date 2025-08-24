package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class Email extends ValueObject<String> {

    /**
     * Email validation regex with support for common email features like plus addressing.
     * <p>
     * Local Part:
     * ^[a-zA-Z0-9][-.+_a-zA-Z0-9]*
     * - Must start with a letter or number
     * - Can contain letters, numbers, dots, hyphens, plus signs and underscores
     * - Supports plus addressing (user+tag@domain.com)
     * <p>
     * Domain Part:
     * [a-zA-Z0-9][-a-zA-Z0-9]*
     * - Must start with a letter or number
     * - Can contain letters, numbers and hyphens
     * <p>
     * Subdomains and TLD:
     * (\.[a-zA-Z0-9][-a-zA-Z0-9]*){0,4}\.[a-zA-Z]{2,}
     * - Limited to a maximum of 4 subdomains
     * - Must end with TLD of at least 2 letters
     */
    private static final String DEFAULT_EMAIL_REGEX =
            "^[a-zA-Z0-9][-.+_a-zA-Z0-9]*@" +
                    "[a-zA-Z0-9][-a-zA-Z0-9]*" +
                    "(?:\\.[a-zA-Z0-9][-a-zA-Z0-9]*){0,4}" +
                    "\\.[a-zA-Z]{2,}$";

    public Email(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        if (value == null) {
            throw new DomainException("email.cannot.be.null");
        }

        if (value.isEmpty()) {
            throw new DomainException("email.cannot.be.null");
        }

        if (!value.matches(DEFAULT_EMAIL_REGEX)) {
            throw new DomainException("email.is.not.valid");
        }
    }
}
