package dev.com.soat.autorepairshop.infrastructure.util;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.shared.masker.DocumentMasker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentMaskerTest {

    @Test
    @DisplayName("Deve mascarar um CPF corretamente")
    void shouldMaskCpf() {
        String cpf = "12869691076";
        String maskedCpf = DocumentMasker.mask(cpf);
        assertEquals("128.***.***-76", maskedCpf);
    }

    @Test
    @DisplayName("Deve mascarar um CNPJ corretamente")
    void shouldMaskCnpj() {
        String cnpj = "48115535000134";
        String maskedCnpj = DocumentMasker.mask(cnpj);
        assertEquals("48.***.***0001-**", maskedCnpj);
    }

    @Test
    @DisplayName("Deve mascarar um CPF formatado corretamente")
    void shouldMaskFormattedCpf() {
        String cpf = "714.300.660-24";
        String maskedCpf = DocumentMasker.mask(cpf);
        assertEquals("714.***.***-24", maskedCpf);
    }

    @Test
    @DisplayName("Deve mascarar um CNPJ formatado corretamente")
    void shouldMaskFormattedCnpj() {
        String cnpj = "50.263.406/0001-35";
        String maskedCnpj = DocumentMasker.mask(cnpj);
        assertEquals("50.***.***0001-**", maskedCnpj);
    }

    @Test
    @DisplayName("Deve retornar a mesma string para um documento com tamanho inválido")
    void shouldReturnSameStringForInvalidLength() {
        String invalidDocument = "12345";
        assertThrows(DomainException.class, () -> DocumentMasker.mask(invalidDocument));
    }

    @ParameterizedTest
    @EmptySource
    @NullSource
    @DisplayName("Deve retornar uma string vazia para um documento nulo/vazio")
    void shouldReturnEmptyStringForNullDocument(String document) {
        assertThrows(DomainException.class, () -> DocumentMasker.mask(document));
    }
}
