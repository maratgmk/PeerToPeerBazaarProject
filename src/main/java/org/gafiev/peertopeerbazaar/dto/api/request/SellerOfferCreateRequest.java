package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO SellerOfferCreateRequest holds data required for creating a seller offer.
 *
 * @param unitCount        The current unit count number.
 * @param offerStatus      The current offer status.
 * @param comment          Special comments related to the seller offer.
 * @param creationDateTime Offer start date/time.
 * @param finishDateTime Offer end date/time.
 * @param productId        Unique product identifier (ID).
 * @param addressId        Unique address identifier (ID).
 * @param createdAt        Timestamp when the SellerOffer entity was first created/recorded.
 */
@Schema(description = "Data required for creating a seller offer.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder(toBuilder = true)
public record SellerOfferCreateRequest(
        @Schema(description = "The current unit count number.", example = "28")
        @PositiveOrZero(message = "Count of units must be zero or positive")
        Integer unitCount,

        @Schema(description = "The current offer status.", example = "OPENED")
        @NotNull @Nonnull
        OfferStatus offerStatus,

        @Schema(description = "Special comments related to the seller offer.", example = "New seller offer opened additionally.")
        @NotBlank @Size(min = 1)
        String comment,

        @Schema(description = "Offer start date/time.", example = "2025-10-15T14:31")
        @NotNull @Nonnull
        LocalDateTime creationDateTime,

        @Schema(description = "Offer end date/time.", example = "2025-10-16T9:31")
        @NotNull @Nonnull @FutureOrPresent(message = "Date Time of finish can not be in the past.")
        LocalDateTime finishDateTime,

        @Schema(description = "Product ID", example = "19")
        @NotNull @Nonnull @Positive(message = "Id of product must be positive")
        Long productId,

        @Schema(description = "Address ID", example = "11")
        @NotNull @Nonnull @Positive(message = "Id of address must be positive")
        Long addressId,

        @Schema(description = "Timestamp when the SellerOffer entity was first created/recorded.", example = "2025-10-15T14:30:45.123456Z")
        @Nonnull @PastOrPresent
        Instant createdAt) {
}
