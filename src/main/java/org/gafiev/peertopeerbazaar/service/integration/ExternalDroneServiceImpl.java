package org.gafiev.peertopeerbazaar.service.integration;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalDroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class ExternalDroneServiceImpl implements ExternalDroneService {



    @Override
    public ExternalDroneResponse getDroneById(Long droneServiceId) {
        return null; //TODO Get запрос URI которого droneServiceId это переменная пути , все остальные будут POST
    }

    @Override
    public Set<ExternalDroneResponse> getAllDronesExternal(ExternalDroneFilterRequest filterRequest) {
        return Set.of(); //POST
    }

    @Override
    public ExternalDroneResponse requestDrone(DeliveryDroneRequest deliveryDroneRequest) {
        return null; //POST
    }

    @Override
    public ExternalDroneResponse requestDroneSchedule(DeliveryDroneRequest deliveryDroneRequest) {
        return null;//POST
    }

    @Override
    public String getCode(AddressCreateRequest addressCreateRequest) {
        return ""; //POST
    }
}
//TODO написать запросы в стороннее приложение с помощью HTTP клиента