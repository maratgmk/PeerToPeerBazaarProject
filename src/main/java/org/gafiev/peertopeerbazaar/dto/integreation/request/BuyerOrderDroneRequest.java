package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

/**
 * приложение запрашивает дрон из внешнего сервиса по DTO данным о заказе покупателя.
 * @param weightKg  общий вес заказа
 * @param volumeLtr  общий объём заказа
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BuyerOrderDroneRequest(
        Double weightKg,
        Double volumeLtr) {
}
