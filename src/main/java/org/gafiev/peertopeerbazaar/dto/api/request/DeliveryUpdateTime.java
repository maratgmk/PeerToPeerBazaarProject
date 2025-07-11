package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

/**
 * запрос на обновление времени доставки
 * @param timeSlot выбранный временной диапазон, полученный от внешнего сервиса.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryUpdateTime(
        @Nonnull @NotNull
        TimeSlotResponse timeSlot) {
}
