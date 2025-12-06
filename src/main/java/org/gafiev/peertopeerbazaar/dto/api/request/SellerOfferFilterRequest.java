package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.PastOrPresent;
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
     //   @Positive Integer unitCountLow,

        /**
         * верхняя граница выборки количества единиц измерения в оффере
         */
   //     @Positive Integer unitCountHigh,

        /**
         * creationDateTimeAfter верхняя временная граница,
         * для поиска событий до этой даты
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        @PastOrPresent LocalDateTime creationDateTimeAfter,
        /**
         *
         * creationDateTimeBefore нижняя временная граница,
         * для поиска событий после этой даты
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        @PastOrPresent LocalDateTime creationDateTimeBefore,

        /**
         * finishDateTimeAfter верхняя временная граница,
         * для поиска событий до этой даты
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime finishDateTimeAfter,

        /**
         * finishDateTimeBefore нижняя временная граница,
         * для поиска событий после этой даты
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime finishDateTimeBefore,

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
//TODO Integer unitCountLow, & Integer unitCountHigh,??? Set<partOfferToBuy> parts; unitCountLow = parts.length???