package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;

import java.math.BigDecimal;

/**
 * ProductUpdateRequest DTO holds data required for updating the existing product.
 *
 * @param name             Product name.
 * @param description      Product description detailing the time and method of creation.
 * @param category         Product category (e.g., type of transportation or storage).
 * @param weight           Product portion unit weight.
 * @param volume           Product portion unit volume.
 * @param price            Price of product unit.
 * @param imageURI         Image of product.
 * @param qrCode           Quick Response Code (QR code) for product information access.
 * @param userId           Unique identifier of the product's author (User ID).
 */
@Schema(description = "")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record ProductUpdateRequest(
        @Schema(description = "Product name", example = "Porcini mushroom")
        @Nonnull @Size(min = 1, max = 49)
        String name,

        @Schema(description = "Product description detailing the time and method of creation.", example = "Today morning picked up.")
        @Nonnull @Size(min = 1, max = 249)
        String description,

        @Schema(description = "Product category (e.g., type of transportation or storage).", example = "Category.PERISHABLE")
        @Nonnull
        Category category,

        @Schema(description = "Product portion unit weight.", example = "1")
        @Nonnull @Positive(message = "Weight can not be negative")
        @Digits(integer = 6, fraction = 2)
        BigDecimal weight,

        @Schema(description = "Product portion unit volume.", example = "1.5")
        @Nonnull @Positive(message = "Volume can not be negative")
        @Digits(integer = 6, fraction = 2)
        BigDecimal volume,

        @Schema(description = "Price of product unit.", example = "2780.85")
        @Nonnull @PositiveOrZero(message = "Price can not be negative")
        @Digits(integer = 12, fraction = 2)
        BigDecimal price,

        @Schema(description = "Image URI of the product.", example = "www.wildchicken.org/image12.jpg")
        @Nonnull @Size(min = 1, max = 249)
        String imageURI,

        @Schema(description = "Quick Response Code (QR code) for product information access.", example = "www.wildchicken.org/info")
        @Nonnull @Size(min = 1, max = 249)
        String qrCode,

        @Schema(description = "Author's unique identifier (User ID).", example = "17")
        @Nonnull @Positive
        Long userId) {
}
