package org.gafiev.peertopeerbazaar.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentCreateRequest(

        @Nonnull @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal amount,

        @Nonnull
        PaymentMode paymentMode,

        @Nonnull
        PaymentStatus paymentStatus,

        @Size(min = 1)
        Set<Long> buyerOrderIds) {
}
