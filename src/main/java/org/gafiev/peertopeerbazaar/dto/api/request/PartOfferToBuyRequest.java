package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PartOfferToBuyRequest(
        Integer unitCount,
        Long sellerOfferId,
        Long buyerOrderId,
        Long basketId ) {
}
