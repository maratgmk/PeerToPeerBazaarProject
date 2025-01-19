package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.response.DroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.delivery.Drone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface DroneService {

    Optional<Drone> getDroneById(Long id);

    List<Drone> getAllDrones();

    Drone createDrone(Drone drone);

    Drone updateDrone(Long id, Drone droneDetails);

    void deleteDrone(Long id);

    Optional<Drone> requestDrone(Delivery delivery);

}
