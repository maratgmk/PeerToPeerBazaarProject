package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PaymentResponse(
        Long id,
        BigDecimal amount,
        PaymentMode paymentMode,
        PaymentStatus paymentStatus,
        LocalDateTime completionDateTime,
        Set<BuyerOrderResponse> buyerOrderResponseSet ){
}
