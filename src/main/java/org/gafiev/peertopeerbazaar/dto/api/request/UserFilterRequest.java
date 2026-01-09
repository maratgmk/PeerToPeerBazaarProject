package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.user.Role;

import java.util.Set;

/**
 * DTO UserFilterRequest holds data required to retrieve a set of users from the database according to the filter criteria.
 *
 * @param ids              Set of User identifiers.
 * @param roles            Set of user's roles.
 * @param ratingSellerLow  The lower limit for the seller's rating.
 * @param ratingSellerHigh The upper limit for the seller's rating.
 * @param ratingBuyerLow   The lower limit for the buyer's rating.
 * @param ratingBuyerHigh  The upper limit for the buyer's rating.
 */
@Schema(description = "Data for the specific filter criteria to retrieve all desired users.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record UserFilterRequest(
        @Schema(description = "Set of IDs corresponding to user identifiers chosen for filter.", example = "[2,5,8]")
        @Size(min = 1) Set<Long> ids,

        @Schema(description = "Set of user's roles chosen for filter.", example = "[\"USER\",\"BUYER\"]")
        Set<Role> roles,

        @Schema(description = "The lower limit for the seller's rating.", example = "3")
        @PositiveOrZero Integer ratingSellerLow,

        @Schema(description = "The upper limit for the seller's rating.", example = "49")
        @PositiveOrZero Integer ratingSellerHigh,

        @Schema(description = "The lower limit for the buyer's rating.", example = "5")
        @PositiveOrZero Integer ratingBuyerLow,

        @Schema(description = "The upper limit for the buyer's rating.", example = "55")
        @PositiveOrZero Integer ratingBuyerHigh) {
}
