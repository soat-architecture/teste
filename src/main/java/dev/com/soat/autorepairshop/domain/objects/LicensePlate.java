package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import lombok.ToString;

@ToString
public class LicensePlate extends ValueObject<String> {

    public static final String LICENSE_PLATE_UNTIL_2018_REGEX = "^[A-Z]{3}-?[0-9]{4}$";
    public static final String LICENSE_PLATE_MERCOSUL_REGEX = "^[A-Z]{3}[0-9][A-Z][0-9]{2}$";

    public LicensePlate(String value) {
        super(value);
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        if(value == null)
            throw new DomainException("licenseplate.cannot.be.null");

        if(!value.matches(LICENSE_PLATE_UNTIL_2018_REGEX) && !value.matches(LICENSE_PLATE_MERCOSUL_REGEX))
            throw new DomainException("licenseplate.invalid");
    }
}
