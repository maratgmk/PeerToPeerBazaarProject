package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DroneCreateRequest(
        @Nonnull
        DroneStatus droneStatus,

        @Nonnull @Positive
        Long deliveryId ) {
}
