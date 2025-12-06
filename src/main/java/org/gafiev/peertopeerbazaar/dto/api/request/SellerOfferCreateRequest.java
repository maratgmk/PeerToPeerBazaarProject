package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.Instant;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record SellerOfferCreateRequest(
        /**
         * количество порций продукта или количество частей = partOfferToBuy
         */
        @PositiveOrZero(message = "Count of unit must be zero or positive")
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
        Long addressId,

        @Nonnull @PastOrPresent
        Instant createdAt) {
}
