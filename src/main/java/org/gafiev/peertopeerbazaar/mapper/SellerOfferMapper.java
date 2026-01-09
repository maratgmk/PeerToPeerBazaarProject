package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting SellerOffer entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class SellerOfferMapper {
    private final  PartOfferToBuyMapper partOfferToBuyMapper;

    /**
     * Converts SellerOffer entity to SellerOfferResponse DTO.
     *
     * @param sellerOffer SellerOffer entity.
     * @return SellerOfferResponse DTO.
     */
    public SellerOfferResponse toSellerOfferResponse(SellerOffer sellerOffer) {
        return SellerOfferResponse.builder()
                .id(sellerOffer.getId())
                .offerStatus(sellerOffer.getOfferStatus())
                .comment(sellerOffer.getComment())
                .creationDateTime(sellerOffer.getCreationDateTime())
                .finishDateTime(sellerOffer.getFinishDateTime())
                .productId(sellerOffer.getProduct().getId() != null ? sellerOffer.getProduct().getId() : null)
                .userId(sellerOffer.getSeller().getId() != null ? sellerOffer.getSeller().getId() : null)
                .addressId(sellerOffer.getAddress() != null ? sellerOffer.getAddress().getId() : null)
                .createdAt(sellerOffer.getCreatedAt())
                .partOfferToBuyResponseList(partOfferToBuyMapper.toPartOfferToBuyResponseList(sellerOffer.getPartOfferToBuyList()))
                .build();
    }

    /**
     * Converts Set of SellerOffer entities to Set of SellerOfferResponse DTOs.
     *
     * @param sellerOfferSet Set of SellerOffer entities.
     * @return Set of SellerOfferResponse DTOs.
     */
    public Set<SellerOfferResponse> toSellerOfferResponseSet(Set<SellerOffer> sellerOfferSet){
        return  sellerOfferSet == null ? null :  sellerOfferSet.stream()
                .map(this::toSellerOfferResponse)
                .collect(Collectors.toSet());
    }
}
