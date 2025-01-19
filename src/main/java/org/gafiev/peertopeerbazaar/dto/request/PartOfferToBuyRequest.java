package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PartOfferToBuyRequest(
        Integer unitCount,
        Long sellerOfferId,
        Long buyerOrderId,
        Long basketId ) {
}
