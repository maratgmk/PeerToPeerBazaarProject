package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.PartOfferToBuyResponse;
import org.gafiev.peertopeerbazaar.entity.order.Basket;
import org.gafiev.peertopeerbazaar.entity.order.PartOfferToBuy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting PartOfferToBuy entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class PartOfferToBuyMapper {

    /**
     * Converts PartOfferToBuy entity to PartOfferToBuyResponse DTO.
     *
     * @param partOfferToBuy PartOfferToBuy entity.
     * @return PartOfferToBuyResponse DTO.
     */
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

    /**
     * Converts List<PartOfferToBuy> entities to List<PartOfferToBuyResponse>  DTOs.
     *
     * @param partOfferToBuyList List<PartOfferToBuy> of PartOfferToBuy entity.
     * @return List<PartOfferToBuyResponse> of PartOfferToBuyResponse DTO.
     */
    public List<PartOfferToBuyResponse> toPartOfferToBuyResponseList(List<PartOfferToBuy> partOfferToBuyList) {
        return partOfferToBuyList == null ? null : partOfferToBuyList.stream()
                .map(this::toPartOfferToBuyResponse)
                .collect(Collectors.toList());
    }
}
