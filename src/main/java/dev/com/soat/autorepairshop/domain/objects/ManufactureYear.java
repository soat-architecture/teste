package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import lombok.ToString;
import java.time.Year;


@ToString
public class ManufactureYear extends ValueObject<Integer> {

    public static final int MIN_MANUFACTURE_YEAR = 1980;

    public ManufactureYear(Integer year) {
        super(year);
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    protected void validate(Integer value) {
        if (value == null) throw new DomainException("manufactureYear.cannot.be.null");
        if (value < MIN_MANUFACTURE_YEAR) throw new DomainException("manufactureYear.older.than.minimum.year");
        if (value > Year.now().getValue()) throw new DomainException("manufactureYear.cannot.be.future");
    }
}
