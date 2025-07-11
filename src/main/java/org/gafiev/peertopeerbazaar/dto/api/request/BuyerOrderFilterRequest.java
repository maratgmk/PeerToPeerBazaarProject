package org.gafiev.peertopeerbazaar.dto.api.request;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;

public record BuyerOrderFilterRequest(
        /**
         * набор идентификаторов заказов покупателей, по которым идёт фильтрация.
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> ids,

        /**
         * статус заказа покупателя
         */
        @NotNull @Nonnull BuyerOrderStatus buyerOrderStatus,

        /**
         * идентификатор покупателя, по которому отбираются заказы
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> buyerIds,

        /**
         * идентификатор платежа, по которому отбираются заказы покупателя
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> paymentIds,

        /**
         * набор идентификаторов заказанных частей, по которым идёт фильтрация.
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> partOfferToBuyIds,

        /**
         * набор идентификаторов поставок, по которым идёт фильтрация.
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> deliveryIds) {
}
