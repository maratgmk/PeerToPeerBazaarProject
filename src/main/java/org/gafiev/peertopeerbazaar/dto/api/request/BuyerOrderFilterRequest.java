package org.gafiev.peertopeerbazaar.dto.api.request;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.BuyerOrderStatus;

import java.util.Set;
@Builder
public record BuyerOrderFilterRequest(
        /**
         * набор идентификаторов заказов покупателей, по которым идёт фильтрация.
         */
        @NotNull @Nonnull @Size(min = 1) Set<Long> ids,
        /**
         * статус заказа покупателя
         */
        @Nullable BuyerOrderStatus buyerOrderStatus,
        /**
         * идентификатор покупателя, по которому отбираются заказы
         */
        @Nullable Set<Long> buyerIds,
        /**
         * идентификатор платежа, по которому отбираются заказы покупателя
         */
        @Nullable Set<Long> paymentIds,
        /**
         * набор идентификаторов заказанных частей, по которым идёт фильтрация.
         */
        @Nullable Set<Long> partOfferToBuyIds,
        /**
         * набор идентификаторов поставок, по которым идёт фильтрация.
         */
        @Nullable Set<Long> deliveryIds) {
}