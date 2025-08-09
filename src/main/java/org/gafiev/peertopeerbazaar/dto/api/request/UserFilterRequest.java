package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.user.Role;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record UserFilterRequest(
        @Size(min = 1) Set<Long> ids,
        Role role,
        @PositiveOrZero Integer ratingSellerLow,
        @PositiveOrZero Integer ratingSellerHigh,
        @PositiveOrZero Integer ratingBuyerLow,
        @PositiveOrZero Integer ratingBuyerHigh ) {
}
