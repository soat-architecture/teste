package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.enums.CarBodyType;
import dev.com.soat.autorepairshop.domain.enums.MotorcycleStyleType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@ToString
public class VehicleType extends ValueObject<String>{

    private CarBodyType carBodyType;
    private MotorcycleStyleType motorcycleStyleType;

    private static final String CAR = "Carro";
    private static final String MOTORCYCLE = "Moto";

    public VehicleType(String value, String carBodyType, String motorcycleStyleType) {
        super(value);

        if (value.equalsIgnoreCase(CAR))
            validateCarBodyType(carBodyType);
        else
            validateMotorcycleStyleType(motorcycleStyleType);
    }

    public CarBodyType getCarBodyType() {
        return carBodyType;
    }

    public MotorcycleStyleType getMotorcycleStyleType() {
        return motorcycleStyleType;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    protected void validate(String value) {
        if (value == null || value.isBlank()) throw new DomainException("vehicle.type.cannot.be.null.or.blank");
        value = value.toLowerCase();
        if (!value.equals(CAR.toLowerCase()) && !value.equals(MOTORCYCLE.toLowerCase())) throw new DomainException("vehicle.type.different");
    }

    private void validateCarBodyType(String type) {
        CarBodyType carBodyType = CarBodyType.fromName(type);
        if (carBodyType == null) throw new DomainException("vehicle.type.car.bodytype.incompatible");
        this.carBodyType = carBodyType;
    }

    private void validateMotorcycleStyleType(String type) {
        MotorcycleStyleType motorcycleStyleType = MotorcycleStyleType.fromName(type);
        if (motorcycleStyleType == null) throw new DomainException("vehicle.type.motorcycle.styletype.incompatible");
        this.motorcycleStyleType = motorcycleStyleType;
    }
}
