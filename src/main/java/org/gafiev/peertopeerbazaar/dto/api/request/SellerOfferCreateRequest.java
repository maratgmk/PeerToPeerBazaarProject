package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SellerOfferCreateRequest(
        @Positive(message = "Count of unit must be more than zero")
        Integer unitCount,

        @Nonnull
        OfferStatus offerStatus,

        @NotBlank @Size(min = 1)
        String comment,

        @Nonnull @FutureOrPresent(message = "Date Time of creation can not be in the past.")
        LocalDateTime creationDateTime,

        @Nonnull @FutureOrPresent(message = "Date Time of finish can not be in the past.")
        LocalDateTime finishedDateTime,

        @Nonnull @Positive(message = "Id of product must be positive")
        Long productId,

        @Nonnull @Positive(message = "Id of address must be positive")
        Long addressId) {
}
