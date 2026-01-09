package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.Set;

/**
 * DTO representing the criteria for filtering drones.
 *
 * @param droneServiceIds Set of unique identifiers assigned by the external service.
 * @param droneIds        Set of unique internal database identifiers.
 * @param deliveryIds     Set of delivery identifiers associated with the drones.
 */
@Schema(description = "Criteria for filtering drones based on various identifiers")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DroneFilterRequest(
        @Schema(description = "Filter by external service provider IDs", example = "[53,75,6,98]")
        @Nullable Set<Long> droneServiceIds,
        @Schema(description = "Filter by internal database IDs", example = "[3,5,6,8]")
        @Nullable Set<Long> droneIds,
        @Schema(description = "Filter by associated delivery IDs", example = "[5,7,11]")
        Set<@Positive Long> deliveryIds) {
}

