package org.gafiev.peertopeerbazaar.service.model;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.mapper.DroneMapper;
import org.gafiev.peertopeerbazaar.repository.DeliveryRepository;
import org.gafiev.peertopeerbazaar.repository.DroneRepository;
import org.gafiev.peertopeerbazaar.repository.specification.DroneSpecification;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalDroneService;
import org.gafiev.peertopeerbazaar.service.model.interfaces.DroneService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DroneServiceImpl implements DroneService {
    private final DroneRepository droneRepository;
    private final ExternalDroneService externalDroneService;
    private final DroneMapper droneMapper;
    private final DeliveryRepository deliveryRepository;

    @Override
    public DroneResponse getDroneById(Long id) {
        Drone drone = droneRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Drone.class, Map.of("id", String.valueOf(id))));
        ExternalDroneResponse externalDroneResponse = externalDroneService.getDroneById(drone.getDroneServiceId());

        return droneMapper.toDroneResponse(drone).toBuilder().status(externalDroneResponse.droneStatus()).build();
    }

    @Override
    public Set<DroneResponse> getAllDrones(DroneFilterRequest filterRequest) {
        List<Drone> droneList = droneRepository.findAll(DroneSpecification.filterByParams(filterRequest));
        return droneMapper.toDroneResponseSet(new HashSet<>(droneList)) ;
    }

    @Override
    public DroneResponse update(Long id, DroneCreateRequest droneRequest) {
        Drone drone = droneRepository.findByIdWithDeliveries(id)
                .orElseThrow(() -> new EntityNotFoundException(Drone.class,Map.of("id", String.valueOf(id))));
        Set<Long>  droneDeliveryIds = drone.getDeliverySet().stream().map(Delivery::getId).collect(Collectors.toSet());

        if(!droneDeliveryIds.equals(droneRequest.deliveryIdsToAdd())){
           Set<Delivery> deliveryNewSet = droneRequest.deliveryIdsToAdd().stream()
                   .map(deliveryId -> deliveryRepository.findById(deliveryId)
                           .orElseThrow(() -> new EntityNotFoundException(Delivery.class,Map.of("id", String.valueOf(deliveryId)))))
                   .collect(Collectors.toSet());
            drone.setDeliverySet(deliveryNewSet);
        }
        if(!droneDeliveryIds.equals(droneRequest.deliveryIdsToRemove())){
            Set<Delivery> deliveryNewSet = droneRequest.deliveryIdsToRemove().stream()
                    .map(deliveryId -> deliveryRepository.findById(deliveryId)
                            .orElseThrow(() -> new EntityNotFoundException(Delivery.class,Map.of("id", String.valueOf(deliveryId)))))
                    .collect(Collectors.toSet());
            drone.setDeliverySet(deliveryNewSet);
        }
        droneRepository.save(drone);


        return droneMapper.toDroneResponse(drone);
    }


}
//   TODO написать методы из нашей БД