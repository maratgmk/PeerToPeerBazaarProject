package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

/**
 * DTO BuyerOrderCreateRequest holds data required for creating a buyer order.
 *
 * @param partOfferToBuyIds Set of PartOfferToBuy identifiers.
 */
@Schema(description = "Data for buyer order creation.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record BuyerOrderCreateRequest(

        @Schema(description = "Set of Ids corresponding to existing seller offer parts in the basket.", example = "[7,11,13,17]")
        @Nonnull @NotNull @Size(min = 1, message = "At least one part offer ID must be provided")
        Set<@Positive Long> partOfferToBuyIds) {
}
