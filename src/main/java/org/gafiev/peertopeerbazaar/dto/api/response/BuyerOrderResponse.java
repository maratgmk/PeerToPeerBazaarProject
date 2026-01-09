package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.time.Instant;
import java.util.Set;

/**
 * DTO BuyerOrderResponse represents a complete BuyerOrder structure.
 * This record is used as a response body in BuyerOrder management API endpoints.
 *
 * @param id                        Unique BuyerOrder identifier.
 * @param buyerId                   Unique Buyer identifier.
 * @param status                    Current BuyerOrderStatus.
 * @param paymentId                 Unique Payment identifier.
 * @param partOfferToBuyResponseSet Set of DTO PartOfferToBuyResponse.
 * @param deliveryIds               Set of Delivery identifiers.
 * @param createdAt                 Timestamp of BuyerOrderResponse creation.
 */
@Schema(description = "Data transfer object (DTO) representing an buyer order for API responses.")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuyerOrderResponse(
        @Schema(description = "Unique BuyerOrder identifier.", example = "5")
        Long id,
        @Schema(description = "Unique User identifier.", example = "9")
        Long buyerId,
        @Schema(description = "Current BuyerOrderStatus.", example = "DELIVERED")
        BuyerOrderStatus status,
        @Schema(description = "Unique Payment identifier.", example = "39")
        Long paymentId,
        @Schema(description = "Set of part offer to buy DTO responses")
        Set<PartOfferToBuyResponse> partOfferToBuyResponseSet,
        @Schema(description = "Set of Delivery identifiers.", example = "[2,5,8,]")
        Set<Long> deliveryIds,
        @Schema(description = "Time of buyer order response creation.", example = "2025-10-15T14:30:45.123456Z")
        Instant createdAt) {
}
