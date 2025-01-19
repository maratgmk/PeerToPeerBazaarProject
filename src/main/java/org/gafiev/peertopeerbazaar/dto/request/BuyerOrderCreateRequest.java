package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuyerOrderCreateRequest(

        @Nonnull
        DeliveryCreateRequest delivery,

        @Nonnull
        @Size(min = 1, message = "At least one part offer ID must be provided")
        Set<@Positive Long> partOfferToBuyIds) {
}
