package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

/**
 * The DroneResponse DTO represents a complete drone structure.
 * Used as the response body in drone management API endpoints.
 *
 * @param id             Unique internal identifier of the drone.
 * @param droneServiceId Unique identifier assigned by the external service.
 * @param status         Current operational status of the drone.
 * @param deliveryIds    Set of associated delivery identifiers.
 */
@Schema(description = "Detailed information about a drone, including internal and external identifiers")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record DroneResponse(
        @Schema(description = "Internal database ID", example = "78")
        Long id,
        @Schema(description = "External service provider ID", example = "578")
        Long droneServiceId,
        @Schema(description = "Current operational status", example = "CRASH_LANDING")
        DroneStatus status,
        @Schema(description = "Set of associated delivery IDs", example = "[3,5,8]")
        Set<Long> deliveryIds) {
}
