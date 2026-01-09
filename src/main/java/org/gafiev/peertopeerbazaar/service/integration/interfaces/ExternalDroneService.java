package org.gafiev.peertopeerbazaar.service.integration.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.AddressCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.DeliveryDroneRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalDroneFilterRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalDroneResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

/**
 * Service interface for interacting with the external drone service.
 * This interface defines the contract for drone assignment,
 * retrieving available delivery time slots, and verifying serviceability of specified addresses.
 */
public interface ExternalDroneService {
    /**
     * Retrieves drone information from the external service by the drone's service identifier.
     *
     * @param droneServiceId the unique identifier of the drone in the external service.
     * @return a DTO containing the drone information.
     */
    ExternalDroneResponse getDroneById(Long droneServiceId);

    /**
     * Retrieves a set of drones from the external service based on the provided filtering criteria.
     *
     * @param filterRequest the criteria for searching and filtering drones.
     * @return a set of DTOs representing the filtered drones.
     */
    Set<ExternalDroneResponse> getAllDronesExternal(ExternalDroneFilterRequest filterRequest);

    /**
     * Requests a drone from the external service for a specific delivery.
     *
     * @param deliveryDroneRequest the delivery details used to match an appropriate drone.
     * @return a DTO containing the assigned drone's information.
     */
    ExternalDroneResponse requestDrone(DeliveryDroneRequest deliveryDroneRequest);

    /**
     * Retrieves a set of available delivery time slots based on the drone delivery request.
     *
     * @param deliveryDroneRequest the delivery details used to calculate available slots.
     * @return a set of available time slots.
     */
    Set<TimeSlotResponse> requestDroneSchedule(DeliveryDroneRequest deliveryDroneRequest);

    /**
     * Updates the drone's status in the external service (e.g., after delivery completion,
     * schedule changes, or in case of an accident).
     *
     * @param droneServiceId the unique identifier of the drone in the external service.
     * @param status the new status to be assigned.
     * @return the external service response confirming the status update.
     */
    ExternalDroneResponse changeStatus(Long droneServiceId, DroneStatus status);

    /**
     * Checks if the specified address is serviceable by the drone delivery system.
     *
     * @param addressCreateRequest the address details to be verified.
     * @return a serviceability code (enum-based string).
     */
    String getCode(AddressCreateRequest addressCreateRequest);

    /**
     * Cancels a drone assignment or reservation via the external service.
     *
     * @param droneServiceId the unique identifier of the drone in the external service.
     * @param deliveryId the unique identifier of the delivery to be cancelled.
     * @return the external service response confirming the cancellation.
     */
    ExternalDroneResponse cancelDrone(Long droneServiceId,Long deliveryId);
}

