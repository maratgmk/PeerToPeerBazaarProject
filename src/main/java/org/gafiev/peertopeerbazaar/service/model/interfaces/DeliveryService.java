package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.DeliveryUpdateTime;
import org.gafiev.peertopeerbazaar.dto.api.response.DeliveryResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;

import java.util.List;
import java.util.Set;

/**
 * Service interface for managing delivery operations.
 */
public interface DeliveryService {

    /**
     * @param id Delivery ID.
     * @return Delivery details.
     */
    DeliveryResponse getDeliveryById(Long id);

    /**
     * @param buyerOrderId Associated buyer order ID.
     * @return Deliveries linked to the order.
     */
    Set<DeliveryResponse> getMyDeliveriesByBuyerOrderId(Long buyerOrderId);

    /**
     * @param filterRequest Filter criteria.
     * @return Deliveries matching the criteria.
     */
    Set<DeliveryResponse> getAllDeliveriesByFilter(DeliveryFilterRequest filterRequest);

    /**
     * @param request Data for creation.
     * @return Created delivery details.
     */
    DeliveryResponse create(DeliveryCreateRequest request);

    /**
     * Fetches time slots from external drone service.
     *
     * @param id Delivery ID.
     * @return Available time slots.
     */
    List<TimeSlotResponse> takeTimeSlots(Long id);

    /**
     * /**
     * Assigns drone for the buyer's chosen time window.
     *
     * @param id            Delivery ID.
     * @param updateRequest Chosen time window.
     * @return Updated delivery details.
     */
    DeliveryResponse assignDroneForDelivery(Long id, DeliveryUpdateTime updateRequest);

    /**
     * @param id     Delivery ID.
     * @param newStatus New delivery newStatus.
     * @return Updated delivery details.
     */
    DeliveryResponse updateStatus(Long id, DeliveryStatus newStatus);
    /**
     *
     * @param id Delivery ID.
     */
    void deleteDelivery(Long id);
}
