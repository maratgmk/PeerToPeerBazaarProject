package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

/**
 * DTO used for updating the delivery time, containing the new time slot.
 *
 * @param timeSlot The new time slot details provided by the external drone service.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryUpdateTime(
        @Schema(description = "The updated time slot details")
        @Nonnull @NotNull
        TimeSlotResponse timeSlot) {
}
