package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * ProductResponse DTO represents a complete structure of Product entity.
 * Used as the response body  in product management API endpoints.
 *
 * @param id                     Unique product identifier (ID).
 * @param name                   Product name.
 * @param description            Product description detailing the time and method of creation.
 * @param category               Product category (e.g., type of transportation or storage).
 * @param portionUnit            Unit of measurement used to count the product quantity.
 * @param weight                 Product portion unit weight.
 * @param volume                 Product portion unit volume.
 * @param price                  Price of product unit.
 * @param imageURI               Image of product.
 * @param qrCode                 Quick Response Code (QR code) for product information access.
 * @param userId                 Unique author (User) identifier.
 * @param createdAt              Timestamp of when the ProductResponse DTO was created.
 * @param sellerOfferResponseSet Set of SellerOfferResponse DTOs.
 */
@Schema(description = "ProductResponse DTO representing a product for API responses.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ProductResponse(
        @Schema(description = "Product response ID", example = "9")
        Long id,
        @Schema(description = "Product name", example = "Chicken eggs")
        String name,
        @Schema(description = "Product description detailing creation time and method", example = "Chicken free walking. Today morning picked up.")
        String description,
        @Schema(description = "Kind of good or category type", example = "FRAGILE")
        Category category,
        @Schema(description = "Unit of measurement", example = "SET")
        PortionUnit portionUnit,
        @Schema(description = "Product portion unit weight", example = "0.56")
        BigDecimal weight,
        @Schema(description = "Product portion unit volume", example = "1.35")
        BigDecimal volume,
        @Schema(description = "Price of product unit", example = "180.00")
        BigDecimal price,
        @Schema(description = "Image URI of the product", example = "www.wildchicken.org/image17.jpg.")
        String imageURI,
        @Schema(description = "QR Code URL", example = "www.wildchicken.org/info")
        String qrCode,
        @Schema(description = "User ID", example = "7")
        Long userId,
        @Schema(description = "TimeStamp of creation", example = "2025-10-15T14:30:45.123456Z")
        Instant createdAt,
        @Schema(description = "Set of SellerOfferResponse DTOs.")
        Set<SellerOfferResponse> sellerOfferResponseSet
) {
}
