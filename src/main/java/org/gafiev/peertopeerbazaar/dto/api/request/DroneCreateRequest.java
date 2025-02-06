package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DroneCreateRequest(
        Set<@Positive Long>  deliveryIdsToRemove,
        Set<@Positive Long>  deliveryIdsToAdd
        ) {
}
