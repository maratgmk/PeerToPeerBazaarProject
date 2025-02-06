package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.order.OfferStatus;

import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record SellerOfferFilterRequest(
        /**
         * набор идентификаторов офферов, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> ids,

        /**
         * статус оффера
         */
        OfferStatus offerStatus,

        /**
         * нижняя граница выборки количества единиц измерения в оффере
         */
        @Positive Integer unitCountLow,

        /**
         * верхняя граница выборки количества единиц измерения в оффере
         */
        @Positive Integer unitCountHigh,

        /**
         * creationDateTimeEarlier верхняя временная граница,
         * для поиска событий до этой даты
         */
        @PastOrPresent LocalDateTime creationDateTimeEarlier,

        /**
         * creationDateTimeLater нижняя временная граница,
         * для поиска событий после этой даты
         */
        @PastOrPresent LocalDateTime creationDateTimeLater,

        /**
         * finishDateTimeEarlier верхняя временная граница,
         * для поиска событий до этой даты
         */
        @PastOrPresent LocalDateTime finishDateTimeEarlier,

        /**
         * finishDateTimeLater нижняя временная граница,
         * для поиска событий после этой даты
         */
        @PastOrPresent LocalDateTime finishDateTimeLater,

        /**
         * набор идентификаторов продуктов, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> productIds,

        /**
         * набор идентификаторов адресов, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> addressIds,

        /**
         * набор идентификаторов продавцов, по которым идёт фильтрация.
         */
        @Size(min = 1) Set<Long> userIds) {
}
