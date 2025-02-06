package org.gafiev.peertopeerbazaar.dto.api.request;

import jakarta.validation.constraints.Size;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

public record BuyerOrderFilterRequest(
        /**
         * набор идентификаторов заказов покупателей, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> ids,

        /**
         * статус заказа покупателя
         */
        BuyerOrderStatus buyerOrderStatus,

        /**
         * идентификатор покупателя, по которому отбираются заказы
         */
        @Size(min = 1) Set<Long> buyerIds,

        /**
         * идентификатор платежа, по которому отбираются заказы покупателя
         */
        @Size(min = 1) Set<Long> paymentIds,

        /**
         * набор идентификаторов заказанных частей, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> partOfferToBuyIds,

        /**
         * набор идентификаторов поставок, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> deliveryIds) {
}
