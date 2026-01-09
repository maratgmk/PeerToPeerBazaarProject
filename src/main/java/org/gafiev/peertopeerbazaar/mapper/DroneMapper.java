package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting Drone entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class DroneMapper {
    /**
     * Converts Drone entity to DroneResponse DTO.
     *
     * @param drone Drone entity.
     * @return DroneResponse DTO.
     */
    public DroneResponse toDroneResponse(Drone drone) {
        return DroneResponse.builder()
                .id(drone.getId())
                .droneServiceId(drone.getDroneServiceId())
                .status(drone.getDroneStatus())
                .deliveryIds(drone.getDeliverySet().stream().map(Delivery::getId).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Converts Set of Drone entities to Set of DroneResponse DTOs.
     *
     * @param drones Set of Drone entities.
     * @return Set of DroneResponse DTOs.
     */
    public Set<DroneResponse> toDroneResponseSet(Set<Drone> drones) {
        return drones == null ? null : drones.stream()
                .map(this::toDroneResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Converts DroneResponse DTO to Drone entity.
     *
     * @param droneResponse DroneResponse DTO.
     * @return Drone entity.
     */
    public Drone toDrone(DroneResponse droneResponse) {
        return Drone.builder()
                .droneServiceId(droneResponse.droneServiceId())
                .build();
    }

    /**
     * Converts ExternalDroneResponse DTO to Drone entity.
     *
     * @param externalDroneResponse ExternalDroneResponse DTO.
     * @return Drone entity.
     */
    public Drone toDrone(ExternalDroneResponse externalDroneResponse) {
        return Drone.builder()
                .droneServiceId(externalDroneResponse.droneServiceId())
                .droneStatus(externalDroneResponse.droneStatus())
                .build();
    }

    /**
     * Converts ExternalDroneResponse DTO to DroneResponse DTO.
     *
     * @param externalDroneResponse ExternalDroneResponse DTO.
     * @return DroneResponse DTO.
     */
    public DroneResponse toDroneResponse(ExternalDroneResponse externalDroneResponse) {
        return DroneResponse.builder()
                .droneServiceId(externalDroneResponse.droneServiceId())
                .status(externalDroneResponse.droneStatus())
                .build();
    }
}
