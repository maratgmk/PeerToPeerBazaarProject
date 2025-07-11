package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SellerOfferResponse(
        Long id,
        OfferStatus offerStatus,
        String comment,
        LocalDateTime creationDateTime,
        LocalDateTime finishDateTime,
        Long productId,
        Long userId,
        Long addressId,
        List<PartOfferToBuyResponse> partOfferToBuyResponseList) {
}
