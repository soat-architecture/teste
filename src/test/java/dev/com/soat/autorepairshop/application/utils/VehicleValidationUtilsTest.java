package dev.com.soat.autorepairshop.application.utils;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class VehicleValidationUtilsTest {

    @Spy
    @InjectMocks
    private VehicleValidationUtils vehicleValidationUtils;

    @Mock
    private VehicleGateway vehicleGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateVehicleExistenceByLicensePlateException(){
        String licensePlate = "ABC-1234";

        Mockito.when(vehicleGateway.findVehicleByLicensePlate(licensePlate)).thenReturn(Optional.empty());

        NotFoundException domainException = assertThrows(NotFoundException.class,
                () -> vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate));

        Assertions.assertEquals("vehicle.not.found", domainException.getMessage());
        Mockito.verify(vehicleGateway).findVehicleByLicensePlate(licensePlate);
    }

    @Test
    void testValidateVehicleExistenceByLicensePlate(){
        String licensePlate = "ABC-1234";

        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto","45997418000153");

        Mockito.when(vehicleGateway.findVehicleByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicleDomain));

        VehicleDomain result = vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate);

        Assertions.assertEquals(vehicleDomain, result);
        Mockito.verify(vehicleGateway).findVehicleByLicensePlate(licensePlate);
    }

    @Test
    void testValidateNewLicensePlateException(){
        String licensePlate = "ABC-1234";

        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        Mockito.when(vehicleGateway.findVehicleByLicensePlate(licensePlate)).thenReturn(Optional.of(vehicleDomain));

        ConflictException domainException = assertThrows(ConflictException.class,
                () -> vehicleValidationUtils.validateNewLicensePlate(licensePlate));

        Assertions.assertEquals("licenseplate.already.exists", domainException.getMessage());
    }

    @Test
    void testValidateNewLicensePlate(){
        String licensePlate = "ABC-1234";

        Mockito.when(vehicleGateway.findVehicleByLicensePlate(licensePlate)).thenReturn(Optional.empty());

        LicensePlate result = vehicleValidationUtils.validateNewLicensePlate(licensePlate);
        LicensePlate expected = new LicensePlate(licensePlate);

        Assertions.assertEquals(expected, result);
        Mockito.verify(vehicleGateway).findVehicleByLicensePlate(licensePlate);
    }
}