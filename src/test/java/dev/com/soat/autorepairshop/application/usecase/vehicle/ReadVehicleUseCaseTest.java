package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class ReadVehicleUseCaseTest {

    @Spy
    @InjectMocks
    private ReadVehicleUseCase readVehicleUseCase;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private VehicleValidationUtils vehicleValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteMethod() {
        String licensePlate = "ABC-1234";
        VehicleDomain vehicleDomain = VehicleDomain.create(licensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        Mockito.when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate)).thenReturn(vehicleDomain);

        VehicleOutputDTO result = readVehicleUseCase.execute(licensePlate);
        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(vehicleDomain);

        Assertions.assertEquals(expected, result);
        Mockito.verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(licensePlate);
    }
}