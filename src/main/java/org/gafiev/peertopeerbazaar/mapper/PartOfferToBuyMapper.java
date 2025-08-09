package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.PartOfferToBuyResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PartOfferToBuyMapper {


    public PartOfferToBuyResponse toPartOfferToBuyResponse(PartOfferToBuy partOfferToBuy) {
        return PartOfferToBuyResponse.builder()
                .id(partOfferToBuy.getId())
                .sellerOfferId(partOfferToBuy.getSellerOffer().getId())
                .buyerOrderId(partOfferToBuy.getBuyerOrder() != null ? partOfferToBuy.getBuyerOrder().getId() : null)
                .basketIds(partOfferToBuy.getBasketSet().stream()
                        .map(Basket::getId)
                        .collect(Collectors.toSet()))
                .build();
    }

    public List<PartOfferToBuyResponse> toPartOfferToBuyResponseList(List<PartOfferToBuy> partOfferToBuyList) {
        return partOfferToBuyList == null ? null : partOfferToBuyList.stream()
                .map(this::toPartOfferToBuyResponse)
                .collect(Collectors.toList());
    }
}
