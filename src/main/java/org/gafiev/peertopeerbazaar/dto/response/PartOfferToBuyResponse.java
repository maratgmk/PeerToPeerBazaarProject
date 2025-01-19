package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PartOfferToBuyResponse(
        Long id,
        Integer unitCount,
        SellerOffer sellerOffer,
        BuyerOrder buyerOrder,
        Basket basket ) {
}
