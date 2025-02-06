package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record DroneResponse(
        Long id,
        Long droneServiceId,
        DroneStatus status,
        Set<Long> deliveryIds) {
}
