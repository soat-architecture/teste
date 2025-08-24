package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.objects.Document;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;


class VehicleDomainTest {

    private final String licensePlate = "DFH-1579";
    private final Integer manufactureYear = 2019;
    private final String brand = "Fiat";
    private final String model = "Mobi";
    private final String vehicleType = "Carro";
    private final String carBodyType = "Hatch";
    private final String color = "Preto";
    private final String document = "45997418000153";

    @Test
    void creatingVehicleWithoutErros() {
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document);

        Assertions.assertEquals(licensePlate, vehicleDomain.getLicensePlate().getValue());
        Assertions.assertEquals(brand, vehicleDomain.getBrand());
        Assertions.assertEquals(model, vehicleDomain.getModel());
        Assertions.assertEquals(manufactureYear, vehicleDomain.getManufactureYear().getValue());
        Assertions.assertEquals(vehicleType, vehicleDomain.getVehicleType().getValue());
        Assertions.assertEquals(color, vehicleDomain.getColor());
        Assertions.assertEquals(document, vehicleDomain.getDocument().getValue());
    }

    @Test
    void creatingVehicleWithoutBrand() {
        Assertions.assertThrows(DomainException.class, () -> VehicleDomain.create(licensePlate, "", model, manufactureYear,
                vehicleType, carBodyType, null, color, document));
    }

    @Test
    void creatingVehicleWithoutModel() {
        Assertions.assertThrows(DomainException.class, () -> VehicleDomain.create(licensePlate, brand, "", manufactureYear,
                vehicleType, carBodyType, null, color, document));
    }

    @Test
    void creatingVehicleWithoutColor() {
        Assertions.assertThrows(DomainException.class, () -> VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, "", document));
    }

    @Test
    void testChangeLicensePlate() {
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document);

        LicensePlate newLicensePlate = new LicensePlate("ABC-1234");
        vehicleDomain.changeLicensePlate(newLicensePlate);

        Assertions.assertEquals(newLicensePlate.getValue(), vehicleDomain.getLicensePlate().getValue());
    }

    @Test
    void testChangeColor() {
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document);

        vehicleDomain.changeColor("Vermelho");

        Assertions.assertEquals("Vermelho", vehicleDomain.getColor());
    }

    @Test
    void testChangeColorException() {
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document);

        Assertions.assertThrows(DomainException.class, () -> vehicleDomain.changeColor(""));
    }

    @Test
    void testChangeOwner() {
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document);

        Document newdocument = Document.from("11.222.333/0001-81");

        vehicleDomain.changeOwner(newdocument);

        Assertions.assertEquals(newdocument.getValue(), vehicleDomain.getDocument().getValue());
    }

    @Test
    void testRestoreVehicleDomain(){
        LocalDateTime createdAt = LocalDateTime.of(2025, Month.JULY, 14, 0, 0);

        VehicleDomain vehicleDomain = VehicleDomain.restore(1L, licensePlate, brand, model, manufactureYear,
                vehicleType, carBodyType, null, color, document, createdAt, null, true);

        Assertions.assertEquals(1L, vehicleDomain.getIdentifier());
        Assertions.assertEquals(licensePlate, vehicleDomain.getLicensePlate().getValue());
        Assertions.assertEquals(brand, vehicleDomain.getBrand());
        Assertions.assertEquals(model, vehicleDomain.getModel());
        Assertions.assertEquals(manufactureYear, vehicleDomain.getManufactureYear().getValue());
        Assertions.assertEquals(vehicleType, vehicleDomain.getVehicleType().getValue());
        Assertions.assertEquals(color, vehicleDomain.getColor());
        Assertions.assertEquals(document, vehicleDomain.getDocument().getValue());
        Assertions.assertEquals(createdAt, vehicleDomain.getCreatedAt());
        Assertions.assertNull(vehicleDomain.getUpdatedAt());
        Assertions.assertTrue(vehicleDomain.isActive());
    }

}