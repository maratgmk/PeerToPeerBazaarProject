package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PartOfferToBuyResponse(
        Long id,
        Long  sellerOfferId,
        Long buyerOrderId,
        Set<Long> basketIds) {
}

