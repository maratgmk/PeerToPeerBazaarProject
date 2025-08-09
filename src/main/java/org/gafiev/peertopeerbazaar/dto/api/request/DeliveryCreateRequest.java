package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * запрос на создание доставки.
 * @param buyerOrderId идентификатор заказа покупателя
 * @param addressId идентификатор адреса покупателя
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryCreateRequest(

        @Nonnull @NotNull @Positive(message = "Id of buyer order must be positive number")
        Long buyerOrderId,

        @Nonnull @NotNull @Positive(message = "Id of address must be positive number")
        Long addressId) {
}
