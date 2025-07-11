package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@AllArgsConstructor
public class BasketMapper {
    private PartOfferToBuyMapper partOfferToBuyMapper;

    public BasketResponse toBasketResponse(Basket basket){
        return BasketResponse.builder()
                .id(basket.getId())
                .parts(new HashSet<>(partOfferToBuyMapper.toPartOfferToBuyResponseList(basket.getPartOfferToBuySet().stream().toList())))
                .build();
    }
}
