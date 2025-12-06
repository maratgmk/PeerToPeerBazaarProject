package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.math.BigDecimal;

/**
 * приложение запрашивает дрон из внешнего сервиса по DTO данным о заказе покупателя.
 * @param weightKg  общий вес заказа
 * @param volumeLtr  общий объём заказа
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record BuyerOrderDroneRequest(
        BigDecimal weightKg,
        BigDecimal volumeLtr) {
}
