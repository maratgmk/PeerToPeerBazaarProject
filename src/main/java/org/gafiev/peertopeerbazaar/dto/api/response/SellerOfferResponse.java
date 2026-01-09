package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The SellerOfferResponse DTO represents a complete seller offer structure.
 * Used as the response body in seller offer management API endpoints.
 *
 * @param id                         Unique seller offer identifier.
 * @param offerStatus                Current offer status.
 * @param comment                    Special comments related to the seller offer.
 * @param creationDateTime           Offer start date/time.
 * @param finishDateTime             Offer end date/time.
 * @param productId                  Unique product identifier (ID).
 * @param userId                     Unique seller identifier (ID).
 * @param addressId                  Unique address identifier (ID).
 * @param createdAt                  Timestamp when the SellerOffer entity was first created/recorded.
 * @param partOfferToBuyResponseList A collection of PartOfferToBuy response DTOs.
 */
@Schema(description = "A DTO representing a complete seller offer structure returned by the API.")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellerOfferResponse(
        @Schema(description = "SellerOffer ID", example = "1")
        Long id,
        @Schema(description = "Current offer status.", example = "CANCELLED")
        OfferStatus offerStatus,
        @Schema(description = "Special comments related to the seller offer.", example = "If the weather is hot, the offer might close earlier.")
        String comment,
        @Schema(description = "The start date/time of the offer.", example = "2025-10-15T14:31")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime creationDateTime,
        @Schema(description = "The end date/time of the offer.", example = "2025-10-15T19:45")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime finishDateTime,
        @Schema(description = "Product ID", example = "9")
        Long productId,
        @Schema(description = "Seller ID", example = "3")
        Long userId,
        @Schema(description = "Address ID", example = "17")
        Long addressId,
        @Schema(description = "Timestamp when the entity was created.", example = "2025-10-15T14:30:45.123456Z")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
        Instant createdAt,
        @Schema(description = "A collection of associated PartOfferToBuy response DTOs.")
        List<PartOfferToBuyResponse> partOfferToBuyResponseList) {
}
