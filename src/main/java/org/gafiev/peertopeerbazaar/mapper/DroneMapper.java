package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.DroneResponse;
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
                .droneStatus(drone.getDroneStatus())
                .deliveryId(drone.getDelivery().getId())
                .build();
    }

    public Set<DroneResponse> toDroneResponseSet(Set<Drone> drones) {
        return drones == null ? null : drones.stream()
                .map(this::toDroneResponse)
                .collect(Collectors.toSet());
    }
}
