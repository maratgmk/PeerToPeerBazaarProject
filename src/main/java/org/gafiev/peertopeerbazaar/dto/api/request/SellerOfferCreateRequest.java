package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SellerOfferCreateRequest(
        /**
         * количество порций продукта или количество частей = partOfferToBuy
         */
        @Positive(message = "Count of unit must be more than zero")
        Integer unitCount,

        @NotNull @Nonnull
        OfferStatus offerStatus,

        @NotBlank @Size(min = 1)
        String comment,

        @NotNull @Nonnull
        LocalDateTime creationDateTime,

        @NotNull @Nonnull @FutureOrPresent(message = "Date Time of finish can not be in the past.")
        LocalDateTime finishedDateTime,

        @NotNull @Nonnull @Positive(message = "Id of product must be positive")
        Long productId,

        @NotNull @Nonnull @Positive(message = "Id of address must be positive")
        Long addressId) {
}
