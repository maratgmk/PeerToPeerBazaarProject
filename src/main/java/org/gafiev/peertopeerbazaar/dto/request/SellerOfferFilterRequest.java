package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record SellerOfferFilterRequest(
        @Size(min = 1) Set<Long> ids,
        OfferStatus offerStatus,
        @Positive Integer unitCountLow,
        @Positive Integer unitCountHigh,
        @PastOrPresent LocalDateTime creationDateTimeEarlier,
        @PastOrPresent LocalDateTime creationDateTimeLater,
        @PastOrPresent LocalDateTime finishDateTimeEarlier,
        @PastOrPresent LocalDateTime finishDateTimeLater,
        @Size(min = 1) Set<Long> productIds,
        @Size(min = 1) Set<Long> addressIds,
        @Size(min = 1) Set<Long> userIds
) {
}
