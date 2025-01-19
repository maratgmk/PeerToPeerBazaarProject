package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class BuyerOrderMapper {
    private final DeliveryMapper deliveryMapper;
    private final PartOfferToBuyMapper partOfferToBuyMapper;

    public BuyerOrderResponse toBuyerOrderResponse(BuyerOrder buyerOrder) {
        return BuyerOrderResponse.builder()
                .id(buyerOrder.getId())
                .buyerId(buyerOrder.getBuyer().getId())
                .status(buyerOrder.getBuyerOrderStatus())
                .paymentId(buyerOrder.getPayment().getId())
                .partOfferToBuyResponseSet(partOfferToBuyMapper.toPartOfferToBuyResponseSet(buyerOrder.getPartOfferToBuySet()))
                .deliverySet(deliveryMapper.toDeliveryResponseSet(buyerOrder.getDeliverySet()))
                .build();
    }

    public Set<BuyerOrderResponse> toBuyerOrderResponseSet(Set<BuyerOrder> buyerOrderSet) {
        return buyerOrderSet == null ? null : buyerOrderSet.stream()
                .map(this::toBuyerOrderResponse)
                .collect(Collectors.toSet());
    }
}
