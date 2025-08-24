package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Year;

class ManufactureYearTest {

    @Test
    void creatingManufactureYearWithNullValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new ManufactureYear(null));
        Assertions.assertEquals("manufactureYear.cannot.be.null", domainException.getMessage());
    }

    @Test
    void creatingManufactureYearWithOldYear() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new ManufactureYear(1979));
        Assertions.assertEquals("manufactureYear.older.than.minimum.year", domainException.getMessage());
    }

    @Test
    void creatingManufactureYearWithFutureYear() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new ManufactureYear(Year.now().plusYears(1).getValue()));
        Assertions.assertEquals("manufactureYear.cannot.be.future", domainException.getMessage());
    }

    @Test
    void creatingManufactureYear() {
        ManufactureYear manufactureYear = new ManufactureYear(1980);
        Assertions.assertEquals(1980, manufactureYear.getValue());
    }

}