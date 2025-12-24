package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Positive;

import java.util.Set;

/**
 * DTO BuyerOrderUpdateRequest holds data required for updating a buyer order.
 *
 * @param deliveryIdsToRemove       Set of Delivery identifiers to remove from buyer order.
 * @param deliveryIdsToAdd          Set of Delivery identifiers to add to buyer order.
 * @param partOfferToBuyIdsToRemove Set of PartOfferToBuy identifiers to remove from buyer order.
 * @param partOfferToBuyIdsToAdd    Set of PartOfferToBuy identifiers to add to buyer order.
 */
@Schema(description = "Data for updating the buyer order.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BuyerOrderUpdateRequest(

        @Schema(description = "Set of Ids corresponding to deliveries to remove from buyer order.", example = "[8,9]")
        @Nullable Set<@Positive Long> deliveryIdsToRemove,
        @Schema(description = "Set of Ids corresponding to deliveries to add to buyer order.", example = "[6,7]")
        @Nullable Set<@Positive Long> deliveryIdsToAdd,

        @Schema(description = "Set of Ids corresponding to parts to remove from buyer order.", example = "[2,5,11,17,]")
        @Nullable Set<@Positive Long> partOfferToBuyIdsToRemove,
        @Schema(description = "Set of Ids corresponding to parts to add to buyer order.", example = "[3,4,7,13,19]")
        @Nullable Set<@Positive Long> partOfferToBuyIdsToAdd) {
}
