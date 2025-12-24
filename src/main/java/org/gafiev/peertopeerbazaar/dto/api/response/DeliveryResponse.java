package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;

/**
 *  The DeliveryResponse DTO represents a complete delivery structure.
 *  Used as the response body in delivery management API endpoints.
 *
 * @param id Unique identifier for the delivery.
 * @param deliveryStatus The current lifecycle status of the delivery.
 * @param timeSlot The scheduled time window for the delivery (may be null if not confirmed).
 * @param orderId The buyer's order ID associated with this delivery.
 * @param addressId The destination address ID where the buyer's order will be delivered.
 */
@Schema(description = "Represents detailed information about a delivery")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryResponse(
        @Schema(description = "Delivery ID",example = "11")
        Long id,
        @Schema(description = "Current delivery status", example = "DELAYED")
        DeliveryStatus deliveryStatus,
        @Schema(description = "Scheduled time window for the delivery")
        TimeSlotResponse timeSlot,
        @Schema(description = "Unique identifier of the associated buyer order", example = "5")
        Long orderId,
        @Schema(description = "Unique identifier of the destination address", example = "27")
        Long addressId) {
}
