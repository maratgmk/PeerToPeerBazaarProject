package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.BuyerOrderResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.BuyerOrderDroneRequest;
import org.gafiev.peertopeerbazaar.entity.delivery.Delivery;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting BuyerOrder entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class BuyerOrderMapper {
    private final PartOfferToBuyMapper partOfferToBuyMapper;

    /**
     * Converts BuyerOrder entity to BuyerOrderResponse DTO.
     *
     * @param buyerOrder BuyerOrder entity to convert.
     * @return Resulting BuyerOrderResponse DTO.
     */
    public BuyerOrderResponse toBuyerOrderResponse(BuyerOrder buyerOrder) {
        return BuyerOrderResponse.builder()
                .id(buyerOrder.getId())
                .buyerId(buyerOrder.getBuyer().getId())
                .status(buyerOrder.getBuyerOrderStatus())
                .paymentId(buyerOrder.getPayment().getId())
                .partOfferToBuyResponseSet(new HashSet<>(partOfferToBuyMapper.toPartOfferToBuyResponseList(buyerOrder.getPartOfferToBuySet().stream().toList())))
                .deliveryIds(buyerOrder.getDeliverySet().stream().map(Delivery::getId).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Converts Set of BuyerOrder entities to Set of BuyerOrderResponse DTOs.
     *
     * @param buyerOrderSet Set of BuyerOrder entities.
     * @return Resulting Set of BuyerOrderResponse DTOs.
     */
    public Set<BuyerOrderResponse> toBuyerOrderResponseSet(Set<BuyerOrder> buyerOrderSet) {
        return buyerOrderSet == null ? null : buyerOrderSet.stream()
                .map(this::toBuyerOrderResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Converts BuyerOrder entity to BuyerOrderDroneRequest DTO.
     *
     * @param buyerOrder BuyerOrder entity to convert.
     * @return Resulting BuyerOrderDroneRequest DTO.
     */
    public BuyerOrderDroneRequest toBuyerOrderDroneRequest(BuyerOrder buyerOrder) {
        return BuyerOrderDroneRequest.builder()
                .weightKg(buyerOrder.getWeightKg())
                .volumeLtr(buyerOrder.getVolumeLtr())
                .build();
    }
}
