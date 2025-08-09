package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record PaymentFilterRequest(

        @Size(min = 1) Set<Long> ids,

        @Positive
        @Digits(integer = 8, fraction = 2) BigDecimal amountLow,

        @Positive
        @Digits(integer = 8, fraction = 2) BigDecimal amountHigh,

        PaymentMode paymentMode,

        PaymentStatus paymentStatus,

        @PastOrPresent
        LocalDateTime completionDateTimeEarlier,

        @PastOrPresent
        LocalDateTime completionDateTimeLater) {
}
