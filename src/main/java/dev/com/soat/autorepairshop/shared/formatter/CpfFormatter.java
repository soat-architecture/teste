package dev.com.soat.autorepairshop.shared.formatter;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class CpfFormatter {

    public static String format(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("cpf.cannot.be.empty");
        }

        value = value.replaceAll("\\D", "");

        if (value.length() != 11) {
            throw  new DomainException("cpf.is.invalid");
        }

        return value.substring(0, 3) + "." + value.substring(3, 6) + "." + value.substring(6, 9) + "-" + value.substring(9, 11);
    }

}
