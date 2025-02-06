package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * запрос дрона из внешнего сервиса по DTO данным о доставке.
 * @param dateTime  ожидаемое время доставки заказа
 * @param buyerOrder  DTO послание про заказ во внешний сервис для оформления вызова дрона
 * @param toAddress  адрес доставки заказа до покупателя
 * @param fromAddress  адрес забора заказа от продавца
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DeliveryDroneRequest(
        LocalDateTime dateTime,
        BuyerOrderDroneRequest buyerOrder,
        AddressDroneRequest toAddress,
        AddressDroneRequest fromAddress) {
}
