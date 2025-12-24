package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.Set;

/**
 * DTO BasketResponse represents a complete Basket structure.
 * Used as a response body in Basket management API endpoints.
 *
 * @param id Unique Basket identifier.
 * @param parts Set of PartOfferToBuyResponse DTOs.
 */
@Schema(description = "Data transfer object (DTO) representing basket for API responses.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record BasketResponse(
        @Schema(description = "Basket's unique identifier.", example = "17")
        Long id,
        @Schema(description = "Set of responses for the seller offer parts within the basket.",
                example = "[2,4,8,11].")
        Set<PartOfferToBuyResponse> parts) {
}
