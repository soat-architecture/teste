package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class DeleteVehicleUseCaseTest {

    @Spy
    @InjectMocks
    private DeleteVehicleUseCase deleteVehicleUseCase;

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
                2018,"Carro", "Hatch", "",
                "Preto", "45997418000153");

        when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(licensePlate)).thenReturn(vehicleDomain);
        doNothing().when(vehicleGateway).delete(licensePlate);

        assertDoesNotThrow(() -> deleteVehicleUseCase.execute(licensePlate));

        verify(vehicleGateway).delete(licensePlate);
    }
}