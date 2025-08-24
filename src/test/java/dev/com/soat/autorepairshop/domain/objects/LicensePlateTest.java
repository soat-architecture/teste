package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class LicensePlateTest {

    @Test
    void creatingLicensePlateWithNullValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new LicensePlate(null));
        Assertions.assertEquals("licenseplate.cannot.be.null", domainException.getMessage());
    }

    @Test
    void creatingLicensePlateWithBlankValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new LicensePlate(""));
        Assertions.assertEquals("licenseplate.invalid", domainException.getMessage());
    }

    @Test
    void creatingLicensePlateWithoutCorrectPattern() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new LicensePlate("AXDF-2E509"));
        Assertions.assertEquals("licenseplate.invalid", domainException.getMessage());
    }

    @Test
    void creatingLicensePlateWithOldPattern() {
        LicensePlate licensePlate = new LicensePlate("ABC-1234");
        Assertions.assertEquals("ABC-1234", licensePlate.getValue());
    }

    @Test
    void creatingLicensePlateWithMercosulPattern() {
        LicensePlate licensePlate = new LicensePlate("ABC1D23");
        Assertions.assertEquals("ABC1D23", licensePlate.getValue());
    }
}