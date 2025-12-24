package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.BasketResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.springframework.stereotype.Component;

import java.util.HashSet;

/**
 * Mapper class for converting Basket entity to DTO (Data Transfer Object).
 */
@Component
@AllArgsConstructor
public class BasketMapper {
    private PartOfferToBuyMapper partOfferToBuyMapper;

    /**
     * Converts Basket entity to BasketResponse DTO.
     *
     * @param basket Basket entity.
     * @return Basket response DTO.
     */
    public BasketResponse toBasketResponse(Basket basket){
        return BasketResponse.builder()
                .id(basket.getId())
                .parts(new HashSet<>(partOfferToBuyMapper.toPartOfferToBuyResponseList(
                        basket.getPartOfferToBuySet().stream().toList())))
                .build();
    }
}
