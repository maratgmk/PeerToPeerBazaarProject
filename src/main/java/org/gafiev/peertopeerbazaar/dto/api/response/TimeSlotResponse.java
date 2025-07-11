package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * ответ от оператора дронов, а также запрос от пользователя (приложения PTPB).
 * предлагаемый оператором временной диапазон возможной доставки.
 * @param start начало временного диапазона
 * @param end конец временного диапазона
 */
public record TimeSlotResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime start,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
         LocalDateTime end) {
}
