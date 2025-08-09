package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.dto.api.response.TimeSlotResponse;

/**
 * запрос приложением PTPB дрона из внешнего сервиса по DTO данным о доставке.
 * @param timeSlot  ожидаемое время доставки заказа
 * @param buyerOrder  DTO послание про заказ во внешний сервис для оформления вызова дрона
 * @param toAddress  адрес доставки заказа до покупателя
 * @param fromAddress  адрес забора заказа от продавца
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryDroneRequest(
        TimeSlotResponse timeSlot,
        BuyerOrderDroneRequest buyerOrder,
        AddressDroneRequest toAddress,
        AddressDroneRequest fromAddress) {
}
