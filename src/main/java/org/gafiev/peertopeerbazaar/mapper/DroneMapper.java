package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DroneMapper {

    public DroneResponse toDroneResponse(Drone drone) {
        return DroneResponse.builder()
                .id(drone.getId())
                .droneServiceId(drone.getDroneServiceId())
                .status(drone.getDroneStatus())
                .deliveryIds(drone.getDeliverySet().stream().map(Delivery::getId).collect(Collectors.toSet()))
                .build();
    }

    public Set<DroneResponse> toDroneResponseSet(Set<Drone> drones) {
        return drones == null ? null : drones.stream()
                .map(this::toDroneResponse)
                .collect(Collectors.toSet());
    }

    public Drone toDrone(DroneResponse droneResponse){
        return Drone.builder()
                .droneServiceId(droneResponse.droneServiceId())
                .build();
    }

    public Drone toDrone(ExternalDroneResponse externalDroneResponse){
        return Drone.builder()
                .droneServiceId(externalDroneResponse.droneServiceId())
                .droneStatus(externalDroneResponse.droneStatus())
                .build();
    }

    public DroneResponse toDroneResponse(ExternalDroneResponse externalDroneResponse){
        return DroneResponse.builder()
                .droneServiceId(externalDroneResponse.droneServiceId())
                .status(externalDroneResponse.droneStatus())
                .build();
    }
}
