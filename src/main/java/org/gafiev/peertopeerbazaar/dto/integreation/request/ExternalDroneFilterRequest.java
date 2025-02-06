package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DroneStatus;

import java.util.Set;

/**
 *
 * @param droneServiceIds
 * @param droneStatusSet
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExternalDroneFilterRequest(@Nullable Set<Long> droneServiceIds,
                                         @Nullable Set<DroneStatus> droneStatusSet) {
}
