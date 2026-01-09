package org.gafiev.peertopeerbazaar.dto.integreation.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

/**
 * DTO representing drone state and metadata received from the external drone service.
 *
 * @param droneServiceId Unique identifier assigned by the external service provider.
 * @param droneStatus    Current operational status of the drone.
 * @param errorMessage   Optional details regarding any errors encountered by the external service.
 */
@Schema(description = "Response containing drone status information from the external service")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ExternalDroneResponse(
        @Schema(description = "Unique identifier in the external system", example = "578")
        Long droneServiceId,
        @Schema(description = "Current operational status of the drone", example = "CRASH_LANDING")
        DroneStatus droneStatus,
        @Schema(description = "Error details, if any (null if successful)", example = "Signal lost during descent")
        String errorMessage) {
}
