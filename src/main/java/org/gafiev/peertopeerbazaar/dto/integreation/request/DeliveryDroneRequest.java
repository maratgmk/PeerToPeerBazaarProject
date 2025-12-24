package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

/**
 * DTO that holds delivery data required to request a drone assignment.
 *
 * @param deliveryId  Unique delivery identifier.
 * @param timeSlot    Selected delivery time window.
 * @param buyerOrder  Order parameters (weight, volume) for serviceability verification.
 * @param toAddress   The destination (delivery) address.
 * @param fromAddress The origin (pickup) address.
 */
@Schema(description = "Data required to request an available drone for delivery")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryDroneRequest(
        @Schema(description = "Delivery Id", example = "19")
        Long deliveryId,

        @Schema(description = "The assigned or requested delivery time slot")
        @JsonProperty("timeSlot")
        TimeSlotResponse timeSlot,

        @Schema(description = "Order specifications to check against drone capacity")
        @JsonProperty("buyerOrder")
        BuyerOrderDroneRequest buyerOrder,

        @Schema(description = "The destination address for drone delivery")
        @JsonProperty("toAddress")
        AddressDroneRequest toAddress,

        @Schema(description = "The pickup location address")
        @JsonProperty("fromAddress")
        AddressDroneRequest fromAddress) {
}
