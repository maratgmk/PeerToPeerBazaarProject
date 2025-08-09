package org.gafiev.peertopeerbazaar.dto.integreation.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.time.LocalDateTime;


@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalPaymentResponse(
        @Nullable String paymentUri,
        @Nonnull PaymentStatus status,
        @Nullable LocalDateTime completionDateTime,
        @Nullable String error
) {
}
