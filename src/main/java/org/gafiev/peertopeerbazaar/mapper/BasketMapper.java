package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.BasketResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BasketMapper {
    private PartOfferToBuyMapper partOfferToBuyMapper;

    public BasketResponse toBasketResponse(Basket basket){
        return BasketResponse.builder()
                .id(basket.getId())
                .parts(partOfferToBuyMapper.toPartOfferToBuyResponseSet(basket.getPartOfferToBuySet()))
                .build();
    }
}
