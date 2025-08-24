package dev.com.soat.autorepairshop.shared.formatter;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;

public class CnpjFormatter {

    public static String format(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("cpf.cannot.be.empty");
        }

        value = value.replaceAll("\\D", "");

        if (value.length() != 14) {
            throw  new DomainException("cnpj.is.invalid");
        }

        return value.substring(0, 2) + "." + value.substring(2, 5) + "." + value.substring(5, 8) + "/" + value.substring(8, 12) + "-" + value.substring(12, 14);
    }

}
