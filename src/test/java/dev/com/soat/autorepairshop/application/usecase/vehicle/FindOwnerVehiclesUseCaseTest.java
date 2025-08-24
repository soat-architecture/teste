package dev.com.soat.autorepairshop.application.usecase.vehicle;

import dev.com.soat.autorepairshop.application.mapper.VehicleApplicationMapper;
import dev.com.soat.autorepairshop.application.models.output.VehicleOutputDTO;
import dev.com.soat.autorepairshop.application.models.output.VehiclesOwnerOutputDTO;
import dev.com.soat.autorepairshop.application.utils.ClientValidationUtils;
import dev.com.soat.autorepairshop.domain.entity.ClientDomain;
import dev.com.soat.autorepairshop.domain.entity.VehicleDomain;
import dev.com.soat.autorepairshop.domain.gateway.ClientGateway;
import dev.com.soat.autorepairshop.domain.gateway.VehicleGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FindOwnerVehiclesUseCaseTest {

    @Spy
    @InjectMocks
    private FindOwnerVehiclesUseCase findOwnerVehiclesUseCase;

    @Mock
    private VehicleGateway vehicleGateway;

    @Mock
    private ClientGateway clientGateway;

    @Mock
    private ClientValidationUtils clientValidationUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteMethodFindOwnerVehicles() {
        String document = "11.222.333/0001-81";

        VehicleDomain vehicle1 = VehicleDomain.create("ABC-1234", "Fiat", "Mobi",
                2018, "Carro", "Hatch", "",
                "Preto", document);

        VehicleDomain vehicle2 = VehicleDomain.create("DEF-5678", "Fiat", "Argo",
                2022, "Carro", "Hatch", "",
                "Preto", document);

        List<VehicleDomain> vehicles = List.of(vehicle1, vehicle2);

        ClientDomain clientMock = Mockito.mock(ClientDomain.class);

        Mockito.when(clientValidationUtils.validateClientExistenceByDocument(document)).thenReturn(clientMock);
        Mockito.when(vehicleGateway.findVehiclesByOwner(document)).thenReturn(vehicles);

        VehiclesOwnerOutputDTO result = findOwnerVehiclesUseCase.execute(document);

        List<VehicleOutputDTO> expectedList = vehicles.stream().map(VehicleApplicationMapper::toDTO).toList();
        VehiclesOwnerOutputDTO expected = new VehiclesOwnerOutputDTO(document, expectedList);

        Assertions.assertEquals(expected, result);
        Mockito.verify(clientValidationUtils).validateClientExistenceByDocument(document);
        Mockito.verify(vehicleGateway).findVehiclesByOwner(document);
    }
}