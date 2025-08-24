package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.CreateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.usecase.vehicle.CreateVehicleUseCase;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class CreateVehicleUseCaseTest {

    @Spy
    @InjectMocks
    private CreateVehicleUseCase createVehicleUseCase;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private VehicleValidationUtils vehicleValidationUtils;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private ClientValidationUtils clientValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteMethod() {
        CreateVehicleInputDTO dto = new CreateVehicleInputDTO("ABC-1234", "Fiat", "Mobi",
                2018, "Carro", "Hatch", null, "Preto",
                "45997418000153");

        LicensePlate licensePlate = new LicensePlate("ABC-1234");
        VehicleDomain vehicleDomain = VehicleApplicationMapper.toDomain(dto);

        Mockito.when(vehicleValidationUtils.validateNewLicensePlate(dto.licensePlate())).thenReturn(licensePlate);
        Mockito.when(vehicleGateway.save(vehicleDomain)).thenReturn(vehicleDomain);

        VehicleOutputDTO result = createVehicleUseCase.execute(dto);
        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(vehicleDomain);

        Assertions.assertEquals(expected, result);
        Mockito.verify(vehicleValidationUtils).validateNewLicensePlate(dto.licensePlate());
        Mockito.verify(vehicleGateway).save(vehicleDomain);
    }
}