package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DroneUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.DroneResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing drone operations and external service integrations.
 */

public interface DroneService {

    /**
     * Retrieves drone details by its internal identifier.
     *
     * @param id Unique internal drone ID.
     * @return Drone response DTO.
     */
    DroneResponse getDroneById(Long id);

    /**
     *Retrieves a drone along with its associated buyer order details.
     *
     * @param id Unique internal drone ID.
     * @return Drone response DTO including order information.
     */
    DroneResponse getDroneByIdWithBuyerOrder(Long id);

    /**
     * Searches for drones matching the specified filter criteria.
     *
     * @param filterRequest Filter criteria DTO.
     * @return A set of drones matching the filters.
     */
    Set<DroneResponse> getAllDrones(DroneFilterRequest filterRequest);

    /**
     * Updates drone delivery assignments (adds or removes deliveries).
     *
     * @param id           Unique internal drone ID.
     * @param droneRequest Update request data containing changes.
     * @return Updated drone details.
     */
    DroneResponse update(Long id, DroneUpdateRequest droneRequest);

    /**
     * Fetches time slots from external drone service.
     *
     * @param deliveryId Delivery ID.
     * @return Collection of time slot responses.
     */
    List<TimeSlotResponse> getTimeSlots(Long deliveryId);

    /**
     * Cancels a drone assignment for a specific delivery via external service.
     *
     * @param id         Drone ID.
     * @param deliveryId Delivery ID assigned to this drone.
     * @return Drone details after cancel.
     */
    DroneResponse cancelDrone(Long id, Long deliveryId);
}


