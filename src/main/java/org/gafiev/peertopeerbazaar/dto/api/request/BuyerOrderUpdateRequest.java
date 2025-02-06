package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuyerOrderUpdateRequest(

        @Nullable Set<@Positive Long> deliveryIdsToRemove,
        @Nullable Set<@Positive Long> deliveryIdsToAdd,

        @Nullable Set<@Positive Long> partOfferToBuyIdsToRemove,
        @Nullable Set<@Positive Long> partOfferToBuyIdsToAdd) {
}
