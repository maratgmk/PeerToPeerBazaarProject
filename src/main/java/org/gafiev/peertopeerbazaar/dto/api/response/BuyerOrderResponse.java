package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuyerOrderResponse(
        Long id,
        Long buyerId,
        BuyerOrderStatus status,
        Long paymentId,
        Set<PartOfferToBuyResponse> partOfferToBuyResponseSet,
        Set<Long> deliveryIds) {
}
