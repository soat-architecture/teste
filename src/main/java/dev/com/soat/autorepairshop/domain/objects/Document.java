package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.shared.formatter.CpfFormatter;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.ValidatorException;
import dev.com.soat.autorepairshop.shared.masker.DocumentMasker;
import dev.com.soat.autorepairshop.shared.formatter.CnpjFormatter;

public class Document extends ValueObject<String> {

    private Document(String value) {
        super(value);
    }

    public static Document from(String document) {
        return new Document(document);
    }

    public static String cleanDocument(String document) {
        return from(document).unformat();
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        if (value == null || value.trim().isEmpty()) throw new DomainException("document.cannot.be.null");
        final var cleanValue = this.onlyNumbers();
        var isValidDocument = switch (cleanValue.length()) {
            case 11 -> isValidCPF(cleanValue);
            case 14 -> isValidCNPJ(cleanValue);
            default -> false;
        };
        if (!isValidDocument) throw new DomainException("document.is.not.valid");
    }

    public String onlyNumbers(String value) {
        return value.replaceAll("\\D", "");
    }

    public String onlyNumbers() {
        return this.onlyNumbers(this.value);
    }

    public String mask() {
        return DocumentMasker.mask(this);
    }

    public boolean isCPF() {
        return this.onlyNumbers().length() == 11;
    }

    public boolean isCNPJ() {
        return this.onlyNumbers().length() == 14;
    }

    public String unformat() {
        return this.onlyNumbers();
    }

    public String format() {
        var cleanValue = this.onlyNumbers();
        if (this.isCNPJ()) {
            return CnpjFormatter.format(cleanValue);
        } else if (this.isCPF()) {
            return CpfFormatter.format(cleanValue);
        } else {
            throw  new ValidatorException("document.is.not.valid");
        }
    }

    /**
     * Validates if the provided number is a valid CPF.
     * Performs check digit validation according to rules.
     *
     * @param value The CPF number to validate
     * @return true if it's a valid CPF, false otherwise
     */
    private boolean isValidCPF(String value) {
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') return false;
            digits[i] = c - '0';
        }

        int firstDigit = digits[0];
        boolean allSame = true;
        for (int i = 1; i < 11; i++) {
            if (digits[i] != firstDigit) {
                allSame = false;
                break;
            }
        }
        if (allSame) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }
        int remainder = sum % 11;
        int checkDigit1 = (remainder < 2) ? 0 : 11 - remainder;
        if (digits[9] != checkDigit1) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }
        remainder = sum % 11;
        int checkDigit2 = (remainder < 2) ? 0 : 11 - remainder;
        return digits[10] == checkDigit2;
    }

    /**
     * Validates if the provided number is a valid CNPJ.
     * Performs check digit validation according to rules.
     *
     * @param value The CNPJ number to validate
     * @return true if it's a valid CNPJ, false otherwise
     */
    private boolean isValidCNPJ(String value) {
        int[] digits = new int[14];
        for (int i = 0; i < 14; i++) {
            char c = value.charAt(i);
            if (c < '0' || c > '9') return false;
            digits[i] = c - '0';
        }

        int firstDigit = digits[0];
        boolean allSame = true;
        for (int i = 1; i < 14; i++) {
            if (digits[i] != firstDigit) {
                allSame = false;
                break;
            }
        }
        if (allSame) return false;

        int[] weight1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += digits[i] * weight1[i];
        }
        int remainder = sum % 11;
        int checkDigit1 = (remainder < 2) ? 0 : 11 - remainder;
        if (digits[12] != checkDigit1) return false;

        int[] weight2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += digits[i] * weight2[i];
        }
        remainder = sum % 11;
        int checkDigit2 = (remainder < 2) ? 0 : 11 - remainder;
        return digits[13] == checkDigit2;
    }
}