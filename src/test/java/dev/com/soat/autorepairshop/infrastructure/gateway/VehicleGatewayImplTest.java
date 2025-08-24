package dev.com.soat.autorepairshop.infrastructure.gateway;

import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.infrastructure.repository.VehicleRepository;
import dev.com.soat.autorepairshop.infrastructure.repository.entity.VehicleEntity;
import dev.com.soat.autorepairshop.infrastructure.repository.mapper.VehicleEntityMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class VehicleGatewayImplTest {

    @Spy
    @InjectMocks
    private VehicleGatewayImpl vehicleGateway;

    @Mock
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveMethod() {
        VehicleDomain vehicleDomain = VehicleDomain.create("ABC-1234", "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleEntity entity = VehicleEntityMapper.toEntity(vehicleDomain);

        when(vehicleRepository.save(entity)).thenReturn(entity);

        VehicleDomain result = vehicleGateway.save(vehicleDomain);

        Assertions.assertEquals(vehicleDomain, result);
        verify(vehicleRepository).save(entity);
    }

    @Test
    void testFindVehicleByLicensePlateNotEmpty() {
        String licensePlate = "ABC-1234";

        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleEntity entity = VehicleEntityMapper.toEntity(vehicleDomain);

        when(vehicleRepository.findVehicleByLicensePlate(licensePlate)).thenReturn(entity);

        Optional<VehicleDomain> result = vehicleGateway.findVehicleByLicensePlate(licensePlate);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(vehicleDomain, result.get());
    }

    @Test
    void testFindVehicleByLicensePlateEmpty() {
        String licensePlate = "ABC-1234";

        when(vehicleRepository.findVehicleByLicensePlate(licensePlate)).thenReturn(null);

        Optional<VehicleDomain> result = vehicleGateway.findVehicleByLicensePlate(licensePlate);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFindVehicleByOwner() {
        String document = "45997418000153";

        VehicleDomain vehicleDomain1 = VehicleDomain.create("ABC-1234", "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleDomain vehicleDomain2 = VehicleDomain.create("DEF-5678", "Fiat", "Argo",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleEntity entity1 = VehicleEntityMapper.toEntity(vehicleDomain1);
        VehicleEntity entity2 = VehicleEntityMapper.toEntity(vehicleDomain2);

        when(vehicleRepository.findVehicleByOwner(document)).thenReturn(List.of(entity1, entity2));

        List<VehicleDomain> result = vehicleGateway.findVehiclesByOwner(document);

        Assertions.assertEquals(List.of(vehicleDomain1, vehicleDomain2), result);
    }

    @Test
    void givenValidLicensePlateWhenDeleteThenInvokeRepositorySoftDelete() {
        String licensePlate = "ABC-1234";

        vehicleGateway.delete(licensePlate);

        verify(vehicleRepository, times(1)).softDeleteByLicensePlate(licensePlate);
    }

}