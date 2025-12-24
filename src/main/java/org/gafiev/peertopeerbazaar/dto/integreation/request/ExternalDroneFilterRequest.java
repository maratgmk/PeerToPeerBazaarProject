package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

/**
 * DTO representing filtering criteria for drones in the external drone service.
 *
 * @param droneServiceIds Set of unique identifiers assigned by the external service.
 * @param droneStatusSet  A set of drone statuses recognized by the external service.
 */
@Schema(description = "Criteria for filtering drones from the external service")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExternalDroneFilterRequest(
        @Schema(description = "Filter by external drone identifiers", example = "[17,38,41]")
        @Nullable Set<Long> droneServiceIds,
        @Schema(description = "Filter by drone statuses in the external system", example = "[TAKE_OFF,LANDED,LOSE_WAY]")
        @Nullable Set<DroneStatus> droneStatusSet) {
}
