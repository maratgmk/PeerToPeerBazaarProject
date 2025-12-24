package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for creating a new delivery.
 *
 * @param buyerOrderId Unique identifier of the buyer's order.
 * @param addressId    Unique identifier of the delivery address.
 */
@Schema(description = "Request data for creating a delivery.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryCreateRequest(
        @Schema(description = "Buyer order ID", example = "19")
        @Nonnull @NotNull @Positive(message = "Id of buyer order must be a positive number")
        Long buyerOrderId,

        @Schema(description = "Address ID", example = "82")
        @Nonnull @NotNull @Positive(message = "Id of address must be a positive number")
        Long addressId) {
}
