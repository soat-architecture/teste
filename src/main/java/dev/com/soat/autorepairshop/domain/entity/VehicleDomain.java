package dev.com.soat.autorepairshop.domain.entity;

import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.objects.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@ToString
@Getter
public class VehicleDomain extends DomainEntity<Long> {

    private final Long identifier;
    private LicensePlate licensePlate;
    private final String brand;
    private final String model;
    private final ManufactureYear manufactureYear;
    private final VehicleType vehicleType;
    private String color;
    private Document document;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private boolean active;

    private VehicleDomain(Long identifier, String licensePlate, String brand, String model, Integer manufactureYear,
                          String vehicleType, String carBodyType, String motorcycleStyleType, String color,
                          String document, LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {

        if (isNullOrBlank(brand)) throw new DomainException("vehicle.brand.cannot.be.null.or.blank");
        if (isNullOrBlank(model)) throw new DomainException("vehicle.model.cannot.be.null.or.blank");
        if (isNullOrBlank(color)) throw new DomainException("vehicle.color.cannot.be.null.or.blank");

        this.identifier = identifier;
        this.licensePlate = new LicensePlate(licensePlate);
        this.brand = brand;
        this.model = model;
        this.manufactureYear = new ManufactureYear(manufactureYear);
        this.vehicleType = new VehicleType(vehicleType, carBodyType, motorcycleStyleType);
        this.color = color;
        this.document = Document.from(document);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public static VehicleDomain create(String licensePlate, String brand, String model, Integer manufactureYear,
                                       String vehicleType, String carBodyType, String motorcycleStyleType, String color,
                                       String document) {

        return new VehicleDomain(null, licensePlate, brand, model, manufactureYear, vehicleType, carBodyType,
                motorcycleStyleType, color, document, null, null, true);
    }

    public static VehicleDomain restore(Long identifier, String licensePlate, String brand, String model,
                                        Integer manufactureYear, String vehicleType, String carBodyType,
                                        String motorcycleStyleType, String color, String document,
                                        LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {

        return new VehicleDomain(identifier, licensePlate, brand, model, manufactureYear, vehicleType, carBodyType,
                motorcycleStyleType, color, document, createdAt, updatedAt, active);
    }

    protected boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public void changeLicensePlate(LicensePlate newLicensePlate) {
        this.licensePlate = newLicensePlate;
    }

    public void changeColor(String newColor) {
        if (isNullOrBlank(newColor)) throw new DomainException("vehicle.color.cannot.be.null.or.blank");
        this.color = newColor;
    }

    public void activate(){
        this.active = true;
    }

    public void changeOwner(Document newOwnerDocument) {
        this.document = newOwnerDocument;
    }

    @Override
    public Long getIdentifier() {
        return this.identifier;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

}
