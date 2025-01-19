package org.gafiev.peertopeerbazaar.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neovisionaries.i18n.CurrencyCode;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentAccountResponse(
        Long id,
        String accountNumber,
        CurrencyCode currency,
        BigDecimal balance,
        PaymentMode paymentMode,
        PaymentStatus paymentStatus,
        LocalDateTime createdDate,
        LocalDateTime updatedDate,
        String bankCode,
        BigDecimal accountLimit,
        Boolean isVerified,
        Long userId) {
}
