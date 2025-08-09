package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.delivery.DeliveryStatus;
import org.gafiev.peertopeerbazaar.entity.time.TimeSlot;

/**
 * ответ от PTPB клиенту, осуществивший запрос доставки.
 * @param id доставки
 * @param deliveryStatus статус доставки
 * @param timeSlot диапазон времени доставки
 * @param orderId идентификатор заказа покупателя
 * @param addressId идентификатор адреса доставки покупателю
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryResponse(
        Long id,
        DeliveryStatus deliveryStatus,
        TimeSlot timeSlot,
        Long orderId,
        Long addressId) {
}
