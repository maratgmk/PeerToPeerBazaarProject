package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.request.PartOfferToBuyRequest;
import org.gafiev.peertopeerbazaar.dto.response.PartOfferToBuyResponse;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PartOfferToBuyMapper {
    public PartOfferToBuyResponse toPartOfferToBuyResponse(PartOfferToBuy partOfferToBuy){
        return PartOfferToBuyResponse.builder()
                .id(partOfferToBuy.getId())
                .unitCount(partOfferToBuy.getUnitCount())
                .sellerOffer(partOfferToBuy.getSellerOffer())
                .buyerOrder(partOfferToBuy.getBuyerOrder())
                .basket(partOfferToBuy.getBasket())
                .build();
    }

    public Set<PartOfferToBuyResponse> toPartOfferToBuyResponseSet(Set<PartOfferToBuy> partOfferToBuySet){
        return partOfferToBuySet == null ? null : partOfferToBuySet.stream()
                .map(this::toPartOfferToBuyResponse)
                .collect(Collectors.toSet());
    }
}
//TODO внедрить зависимости Basket BuyerOrder