package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentUpdateRequest(

        @Nullable @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal amount,

        @Nullable
        PaymentMode paymentMode,

        @Nullable
        PaymentStatus paymentStatus,

        @Nullable
        LocalDateTime completionDateTime,

        @Nullable
        Set<@Positive Long> buyerOrderIdsToAdd,

        @Nullable
        Set<@Positive Long> buyerOrderIdsToRemove) {
}
