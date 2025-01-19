package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellerOfferResponse(
        Long id,
        Integer unitCount,
        OfferStatus offerStatus,
        String comment,
        LocalDateTime creationDateTime,
        LocalDateTime finishDateTime,
        Long productId,
        Long userId,
        Long addressId,
        Set<PartOfferToBuyResponse> partOfferToBuyResponseSet) {
}
