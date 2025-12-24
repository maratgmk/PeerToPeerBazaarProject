package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

import java.util.Set;

/**
 * DTO request describing drone behavior changes for completing deliveries.
 *
 * @param deliveryIdsToRemove Set of delivery identifiers the drone should stop processing.
 * @param deliveryIdsToAdd     Set of delivery identifiers the drone should start carrying.
 */
@Schema(description = "Request to update drone delivery assignments")
@JsonIgnoreProperties(ignoreUnknown = true)
public record DroneUpdateRequest(
        @Schema(description = "Set of delivery IDs to be removed from the drone", example = "[7,11,19]")
        Set<@Positive Long> deliveryIdsToRemove,
        @Schema(description = "Set of delivery IDs to be assigned to the drone", example = "[5,8,17]")
        Set<@Positive Long> deliveryIdsToAdd) {
}
