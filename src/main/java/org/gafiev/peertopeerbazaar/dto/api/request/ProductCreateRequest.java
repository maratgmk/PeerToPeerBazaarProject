package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.product.Category;
import org.gafiev.peertopeerbazaar.entity.product.PortionUnit;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ProductCreateRequest DTO contains data for creating new Product entity.
 *
 * @param name        Product name.
 * @param description Product description detailing the time and method of creation.
 * @param category    Product category (e.g., type of transportation or storage).
 * @param portionUnit Unit of measurement used to count the product quantity.
 * @param weight      Product weight per portion unit.
 * @param volume      Product volume per portion unit.
 * @param price       Price of product unit.
 * @param imageURI    Image Uniform Resource Identifier (URI) for the product.
 * @param qrCode      Quick Response Code (QR code) for product information access.
 * @param userId      Unique author (User) identifier.
 * @param createdAt   Timestamp of when the product entity was created/recorded.
 */
@Schema(description = "Data for creating new Product entity.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record ProductCreateRequest(
        @Schema(description = "Product name", example = "Gooseberry")
        @Nonnull @Size(min = 1, max = 49)
        String name,

        @Schema(description = "Detailing the time and method of product creation.", example = "Yesterday evening in the forest.")
        @Nonnull @Size(min = 1, max = 249)
        String description,

        @Schema(description = "Product category (e.g., type of transportation or storage).", example = "Category.AVOID_HEAT")
        @Nonnull
        Category category,

        @Schema(description = "Unit of measurement used to count the product quantity.", example = "PortionUnit.KILOGRAM")
        @Nonnull
        PortionUnit portionUnit,

        @Schema(description = "Product weight per portion unit.", example = "1")
        @Nonnull @Positive(message = "Weight can not be negative")
        @Digits(integer = 6, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal weight,

        @Schema(description = "Product volume per portion unit.", example = "1")
        @Nonnull @Positive(message = "Volume can not be negative")
        @Digits(integer = 6, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal volume,

        @Schema(description = "Price of product unit.", example = "890.50")
        @Nonnull @PositiveOrZero(message = "Price can not be negative")
        @Digits(integer = 12, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal price,

        @Schema(description = "Image Uniform Resource Identifier (URI) for the product", example = "www.wildchicken.org/image17.jpg.")
        @Nonnull @Size(min = 1, max = 249)
        String imageURI,

        @Schema(description = "Quick Response Code (QR code) URL for product information access", example = "www.wildchicken.org/info")
        @Nonnull @Size(min = 1, max = 249)
        String qrCode,

        @Schema(description = "Unique author (User) identifier.", example = "7")
        @Nonnull @Positive(message = "Id of user must be positive")
        Long userId,

        @Schema(description = "Timestamp of when the product entity was created/recorded.", example = "2025-10-15T14:30:45.123456Z")
        @Nonnull @PastOrPresent
        Instant createdAt) {
}
