package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PaymentRedirectResponse(
        /**
         * идентификатор платежа
         */
        Long id,
        /**
         * WEB адрес платежной страницы, куда будет направлен покупатель
         */
        String paymentPageUri){
}
