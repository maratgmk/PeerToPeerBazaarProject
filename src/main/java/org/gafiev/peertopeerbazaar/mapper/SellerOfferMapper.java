package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.SellerOfferResponse;
import org.gafiev.peertopeerbazaar.entity.order.SellerOffer;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SellerOfferMapper {
    private PartOfferToBuyMapper partOfferToBuyMapper;

    public SellerOfferResponse toSellerOfferResponse(SellerOffer sellerOffer) {
        return SellerOfferResponse.builder()
                .id(sellerOffer.getId())
                .unitCount(sellerOffer.getUnitCount())
                .offerStatus(sellerOffer.getOfferStatus())
                .creationDateTime(sellerOffer.getCreationDateTime())
                .finishDateTime(sellerOffer.getFinishDateTime())
                .productId(sellerOffer.getProduct().getId() != null ? sellerOffer.getProduct().getId() : null)
                .userId(sellerOffer.getSeller().getId() != null ? sellerOffer.getSeller().getId() : null)
                .addressId(sellerOffer.getAddress() != null ? sellerOffer.getAddress().getId() : null)
                .partOfferToBuyResponseSet(partOfferToBuyMapper.toPartOfferToBuyResponseSet(sellerOffer.getPartOfferToBuySet()))
                .build();
    }
    public Set<SellerOfferResponse> toSellerOfferResponseSet(Set<SellerOffer> sellerOfferSet){
        return  sellerOfferSet == null ? null :  sellerOfferSet.stream()
                .map(this::toSellerOfferResponse)
                .collect(Collectors.toSet());
    }
}
