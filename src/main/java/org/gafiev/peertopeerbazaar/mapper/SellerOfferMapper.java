package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SellerOfferMapper {
    private final  PartOfferToBuyMapper partOfferToBuyMapper;

    public SellerOfferResponse toSellerOfferResponse(SellerOffer sellerOffer) {
        return SellerOfferResponse.builder()
                .id(sellerOffer.getId())
                .offerStatus(sellerOffer.getOfferStatus())
                .creationDateTime(sellerOffer.getCreationDateTime())
                .finishDateTime(sellerOffer.getFinishDateTime())
                .productId(sellerOffer.getProduct().getId() != null ? sellerOffer.getProduct().getId() : null)
                .userId(sellerOffer.getSeller().getId() != null ? sellerOffer.getSeller().getId() : null)
                .addressId(sellerOffer.getAddress() != null ? sellerOffer.getAddress().getId() : null)
                .partOfferToBuyResponseList(partOfferToBuyMapper.toPartOfferToBuyResponseList(sellerOffer.getPartOfferToBuyList()))
                .build();
    }
    public Set<SellerOfferResponse> toSellerOfferResponseSet(Set<SellerOffer> sellerOfferSet){
        return  sellerOfferSet == null ? null :  sellerOfferSet.stream()
                .map(this::toSellerOfferResponse)
                .collect(Collectors.toSet());
    }
}
