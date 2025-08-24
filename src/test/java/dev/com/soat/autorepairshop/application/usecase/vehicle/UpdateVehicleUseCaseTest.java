package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.input.UpdateVehicleInputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.application.utils.VehicleValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import dev.com.soat.autorepairshop.domain.objects.LicensePlate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UpdateVehicleUseCaseTest {

    @Spy
    @InjectMocks
    private UpdateVehicleUseCase updateVehicleUseCase;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private VehicleValidationUtils vehicleValidationUtils;

    @Mock
    private ClientValidationUtils clientValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteMethodUpdatingLicensePlate() {
        String actualLicensePlate = "ABC-1234";
        UpdateVehicleInputDTO dto = new UpdateVehicleInputDTO("ABC1D23", null, null, false);

        VehicleDomain vehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        LicensePlate newLicensePlate = new LicensePlate(dto.newLicensePlate());

        VehicleDomain newVehicleDomain = VehicleDomain.create(dto.newLicensePlate(), "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(actualLicensePlate)).thenReturn(vehicleDomain);
        when(vehicleValidationUtils.validateNewLicensePlate(dto.newLicensePlate())).thenReturn(newLicensePlate);
        when(vehicleGateway.save(newVehicleDomain)).thenReturn(newVehicleDomain);

        VehicleOutputDTO result = updateVehicleUseCase.execute(dto, actualLicensePlate);

        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(newVehicleDomain);

        assertEquals(expected, result);
        verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(actualLicensePlate);
        verify(vehicleValidationUtils).validateNewLicensePlate(dto.newLicensePlate());
        verify(vehicleGateway).save(newVehicleDomain);
    }

    @Test
    void testExecuteMethodUpdatingOwner() {
        String actualLicensePlate = "ABC-1234";
        UpdateVehicleInputDTO dto = new UpdateVehicleInputDTO("", "11.222.333/0001-81", null, false);

        VehicleDomain vehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleDomain newVehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "11.222.333/0001-81");

        ClientDomain clientDomainMock = mock(ClientDomain.class);

        when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(actualLicensePlate)).thenReturn(vehicleDomain);
        when(clientValidationUtils.validateClientExistenceByDocument(dto.newLicensePlate())).thenReturn(clientDomainMock);
        when(vehicleGateway.save(newVehicleDomain)).thenReturn(newVehicleDomain);

        VehicleOutputDTO result = updateVehicleUseCase.execute(dto, actualLicensePlate);

        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(newVehicleDomain);

        assertEquals(expected, result);
        verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(actualLicensePlate);
        verify(vehicleValidationUtils, times(0)).validateNewLicensePlate(dto.newLicensePlate());
        verify(vehicleGateway).save(newVehicleDomain);
    }

    @Test
    void testExecuteMethodUpdatingColor() {
        String actualLicensePlate = "ABC-1234";
        UpdateVehicleInputDTO dto = new UpdateVehicleInputDTO("", "", "Branco", false);

        VehicleDomain vehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleDomain newVehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Branco", "45997418000153");

        when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(actualLicensePlate)).thenReturn(vehicleDomain);
        when(vehicleGateway.save(newVehicleDomain)).thenReturn(newVehicleDomain);

        VehicleOutputDTO result = updateVehicleUseCase.execute(dto, actualLicensePlate);

        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(newVehicleDomain);

        assertEquals(expected, result);
        verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(actualLicensePlate);
        verify(vehicleValidationUtils, times(0)).validateNewLicensePlate(dto.newLicensePlate());
        verify(vehicleGateway).save(newVehicleDomain);
    }

    @Test
    void testExecuteMethodActivatingVehicle() {
        String actualLicensePlate = "ABC-1234";
        UpdateVehicleInputDTO dto = new UpdateVehicleInputDTO("", "", "", true);

        VehicleDomain vehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        VehicleDomain newVehicleDomain = VehicleDomain.create(actualLicensePlate, "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", "45997418000153");

        when(vehicleValidationUtils.validateVehicleExistenceByLicensePlate(actualLicensePlate)).thenReturn(vehicleDomain);
        when(vehicleGateway.save(newVehicleDomain)).thenReturn(newVehicleDomain);

        VehicleOutputDTO result = updateVehicleUseCase.execute(dto, actualLicensePlate);

        VehicleOutputDTO expected = VehicleApplicationMapper.toDTO(newVehicleDomain);

        assertEquals(expected, result);
        verify(vehicleValidationUtils).validateVehicleExistenceByLicensePlate(actualLicensePlate);
        verify(vehicleValidationUtils, times(0)).validateNewLicensePlate(dto.newLicensePlate());
        verify(vehicleGateway).save(newVehicleDomain);
    }
}