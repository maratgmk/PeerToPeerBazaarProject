package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

/**
 * Data Transfer Object representing the details of a part offer.
 * As this entity is always managed as part of other entities,
 * this DTO is included in API responses of entities that manage part-related data,
 * such as Seller Offers, Baskets, or Buyer Orders.
 *
 * @param id Unique identifier of the part.
 * @param sellerOfferId Identifier of the seller offer this part belongs to.
 * @param buyerOrderId Identifier of the order this part belongs to (null if the part is still in a basket or pending sale).
 * @param basketIds Set of IDs for the baskets currently containing this part.
 */
@Schema(description = "Data representation of a product unit (partOfferToBuy) offered for sale")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PartOfferToBuyResponse(
        @Schema(description = "Unique identifier of the part", example = "[17,19,21,27,31]")
        Long id,
        @Schema(description = "Identifier of the seller offer this part belongs to", example = "2")
        Long  sellerOfferId,
        @Schema(description = "Identifier of the buyer order (null if not ordered)", example = "3")
        Long buyerOrderId,
        @Schema(description = "Set of IDs of the baskets that include this part", example = "[2,3,7]")
        Set<Long> basketIds) {
}

