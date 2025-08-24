package dev.com.soat.autorepairshop.shared.masker;

import dev.com.soat.autorepairshop.domain.objects.Document;

public class DocumentMasker {

    public static String mask(String doc) {
        var document = Document.from(doc);
        return mask(document);
    }

    public static String mask(Document document) {
        if (document.isCNPJ()) {
            return maskCnpj(document.onlyNumbers());
        } else if (document.isCPF()) {
            return maskCpf(document.onlyNumbers());
        } else {
            return document.getValue();
        }
    }

    private static String maskCpf(String cpf) {
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9, 11);
    }

    private static String maskCnpj(String cnpj) {
        return cnpj.substring(0, 2) + ".***.***" + cnpj.substring(8, 12) + "-**";
    }
}
