package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DroneResponse(
        Long id,
        DroneStatus droneStatus,
        Long deliveryId) {
}
