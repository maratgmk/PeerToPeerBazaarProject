package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;

import java.math.BigDecimal;
import java.util.Set;

/**
 * The ProductFilterRequest DTO contains data for creating specific filter criteria.
 *
 * @param ids                 Set of product identifiers.
 * @param name                Product name.
 * @param descriptionKeyWords Set of keywords to search in the product description.
 * @param category            Product category (e.g., type of transportation or storage).
 * @param priceHigher         The upper limit for the product price.
 * @param priceLower          The lower limit for the product price.
 * @param qrCode              Quick Response Code (QR code) for product information access.
 */
@Schema(description = "Data for the specific filter to get all products of interest.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ProductFilterRequest(
        @Schema(description = "Set of product IDs.", example = "[3,5,7]")
        @Size(min = 1) Set<Long> ids,

        @Schema(description = "Product name", example = "Strawberry")
        @Size(min = 1) String name,

        @Schema(description = "Set of keywords to search in the product description.", example = "[fresh,forest,berry]")
        @Size(min = 1) Set<@Size(min = 1) String> descriptionKeyWords,

        @Schema(description = "Product category (e.g., type of transportation or storage).", example = "Category.FRAGILE")
        Category category,

        @Schema(description = "The upper price limit", example = "3000.00")
        @Positive BigDecimal priceHigher,

        @Schema(description = "The lower price limit", example = "500.00")
        @Positive BigDecimal priceLower,

        @Schema(description = "QR Code URL", example = "www.wildberry.org/info")
        @Size(min = 1) String qrCode
) {
}
