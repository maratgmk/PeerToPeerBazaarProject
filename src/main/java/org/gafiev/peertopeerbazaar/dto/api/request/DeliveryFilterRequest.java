package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

import java.util.Set;

/**
 * Data Transfer Object for creating delivery filter criteria.
 * Supports both strict containment and interval overlap logic for time-based filtering.
 *
 * @param ids            Set of delivery identifiers to match.
 * @param deliveryStatus Filter by current delivery status.
 * @param startTimeAfter The start boundary of the time range filter.
 * @param endTimeBefore  The end boundary of the time range filter.
 * @param entirelyWithin If true, matches only deliveries fully contained within the [startTimeAfter, endTimeBefore] range.
 *                       If false, matches any delivery that overlaps with this range.
 * @param buyerOrderId   Identifier of the buyer order associated with the delivery.
 * @param toAddressId    Identifier of the destination address.
 * @param fromAddressId  Identifier of the origin address (pickup location).
 * @param droneId        Identifier of the drone assigned to the delivery.
 */
@Schema(description = "Criteria for filtering deliveries.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record DeliveryFilterRequest(

        @Schema(description = "Set of delivery IDs", example = "[3, 5, 7]")
        Set<Long> ids,

        @Schema(description = "Current delivery status", example = "ON_THE_WAY")
        DeliveryStatus deliveryStatus,

        @Schema(description = "Start of the time range filter (matches against timeSlot start)")
        TimeSlot startTimeAfter,

        @Schema(description = "End of the time range filter (matches against timeSlot end)")
        TimeSlot endTimeBefore,

        @Schema(
                description = "If true, matches only deliveries fully contained within the specified range (Subset mode). " +
                        "If false, matches any delivery active during the range (Intersection mode).",
                defaultValue = "false"
        )
        boolean entirelyWithin,

        @Schema(description = "ID of the buyer order associated with the delivery", example = "17")
        Long buyerOrderId,

        @Schema(description = "Destination address ID", example = "54")
        Long toAddressId,

        @Schema(description = "Origin address ID (pickup location)", example = "41")
        Long fromAddressId,

        @Schema(description = "ID of the drone assigned to the delivery", example = "37")
        Long droneId) {
}
