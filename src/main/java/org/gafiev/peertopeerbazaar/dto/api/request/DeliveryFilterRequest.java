package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record DeliveryFilterRequest(

        /**
         * набор идентификаторов доставок, по которым идёт фильтрация.
         */
        Set<Long> ids,

        /**
         * состояние доставки
         */
        DeliveryStatus deliveryStatus,

        /**
         * timeSlotEarlier верхняя временная граница,
         * для поиска событий до этой временной метки
         */
        TimeSlot timeSlotEarlier,

        /**
         * timeSlotTimeLater нижняя временная граница,
         * для поиска событий после этой временной метки
         */
        TimeSlot timeSlotTimeLater,

        /**
         * идентификатор заказа покупателя, по которому происходит фильтрация
         */
        Long buyerOrderId,

        /**
         * идентификатор адреса покупателя, по которому происходит фильтрация
         */
        Long toAddressId,

        /**
         * идентификатор адреса продавца, по которому происходит фильтрация
         */
        Long fromAddressId,

        /**
         * идентификатор дрона, по которому происходит фильтрация
         */
        Long droneId) {
}
