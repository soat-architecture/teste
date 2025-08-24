package dev.com.soat.autorepairshop.domain.objects;

import dev.com.soat.autorepairshop.domain.enums.CarBodyType;
import dev.com.soat.autorepairshop.domain.enums.MotorcycleStyleType;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VehicleTypeTest {

    @Test
    public void creatingVehicleTypeWithNullValues() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType(null, null, null));
        Assertions.assertEquals("vehicle.type.cannot.be.null.or.blank", domainException.getMessage());
    }

    @Test
    public void creatingVehicleTypeWithBlankValues() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("", "", ""));
        Assertions.assertEquals("vehicle.type.cannot.be.null.or.blank", domainException.getMessage());
    }

    @Test
    public void creatingDifferentVehicleType() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Caminhão", "", ""));
        Assertions.assertEquals("vehicle.type.different", domainException.getMessage());
    }

    @Test
    public void testeValidateCarBodyTypeWithBlankValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Carro", "", ""));
        Assertions.assertEquals("vehicle.type.car.bodytype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateCarBodyTypeWithNullValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Carro", "", ""));
        Assertions.assertEquals("vehicle.type.car.bodytype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateCarBodyTypeWithIncompatibleValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Carro", "", "Trail"));
        Assertions.assertEquals("vehicle.type.car.bodytype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateCarBodyTypeSuccess() {
        VehicleType vehicleType = new VehicleType("Carro", "Hatch", "");
        Assertions.assertEquals(CarBodyType.HATCH, vehicleType.getCarBodyType());
    }

    @Test
    public void testeValidateMotorcycleStyleTypeWithBlankValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Moto", "", ""));
        Assertions.assertEquals("vehicle.type.motorcycle.styletype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateMotorcycleStyleTypeWithNullValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () ->
                new VehicleType("Moto", null, null));
        Assertions.assertEquals("vehicle.type.motorcycle.styletype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateMotorcycleStyleTypeWithIncompatibleValue() {
        DomainException domainException = Assertions.assertThrows(DomainException.class, () -> new VehicleType("Moto", "Hatch", ""));
        Assertions.assertEquals("vehicle.type.motorcycle.styletype.incompatible", domainException.getMessage());
    }

    @Test
    public void testeValidateMotorcycleStyleTypeSuccess() {
        VehicleType vehicleType = new VehicleType("Moto", "", "Trail");
        Assertions.assertEquals(MotorcycleStyleType.TRAIL, vehicleType.getMotorcycleStyleType());
    }

}