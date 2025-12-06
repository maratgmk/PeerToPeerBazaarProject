package org.gafiev.peertopeerbazaar.common;

import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderCreateRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.BuyerOrderFilterRequest;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class TestDataBuyerOrder {

    public static final long EXISTING_BUYER_ORDER_ID = 102L;

    public static BuyerOrder getBuyerOrderSample() {
        return BuyerOrder.builder()
                .buyerOrderStatus(BuyerOrderStatus.CREATED)
                .payment(TestDataPayment.getPaymentCreated())
                .partOfferToBuySet(new HashSet<>())
                .deliverySet(new HashSet<>())
                .createdAt(Instant.now())
                .build();
    }

    public static BuyerOrderFilterRequest getFilter() {
        return BuyerOrderFilterRequest.builder()
                .ids(Set.of(102L, 103L, 104L))
                .buyerIds(Set.of(102L, 103L, 104L))
//                .buyerOrderStatus(BuyerOrderStatus.CREATED)
//                .paymentIds(Set.of(1L,2L,3L))
                .build();
    }

    public static BuyerOrderCreateRequest orderCreateRequest() {
        return null;
    }

}
