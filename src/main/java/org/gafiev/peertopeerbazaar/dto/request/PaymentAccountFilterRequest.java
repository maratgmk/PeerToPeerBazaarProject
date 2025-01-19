package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.neovisionaries.i18n.CurrencyCode;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record PaymentAccountFilterRequest(

        Set<Long> ids,

        Set<Long> userIds,

        @NotBlank
        @Pattern(regexp = "\\d{1,20}", message = "Account number must be numerical")
        String accountNumber,

        CurrencyCode currency,

        PaymentMode paymentMode,

        PaymentStatus paymentStatus,

        @PositiveOrZero
        @Digits(integer = 6, fraction = 2)
        BigDecimal balanceLow,

        @PositiveOrZero
        @Digits(integer = 6, fraction = 2)
        BigDecimal balanceHigh,

        @Pattern(regexp = "^[A-Z0-9]{8,11}$", message = "Bank code must be alphanumerical and has from 8 to 11 symbols")
        String bankCode,

        @PastOrPresent
        LocalDateTime createdDateAfter,

        @PastOrPresent
        LocalDateTime createdDateBefore,

        @PastOrPresent
        LocalDateTime updatedDateAfter,

        @PastOrPresent
        LocalDateTime updatedDateBefore,

        @Digits(integer = 4, fraction = 2)
        BigDecimal accountLimitLow,

        @Digits(integer = 4, fraction = 2)
        BigDecimal accountLimitHigh,

        @AssertFalse
        Boolean isVerified

) {
}
