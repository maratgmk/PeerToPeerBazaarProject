package org.gafiev.peertopeerbazaar.dto.integreation.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.time.LocalDateTime;

/**
 * DTO representing the response from the external payment service.
 * This record encapsulates the data received from the external payment provider,
 * including details about the payment status, completion, and any errors.
 *
 * @param paymentUri         The URI of the payment page on the external payment service side for completing the payment.
 * @param status             The current state of the payment.
 * @param completionDateTime The timestamp provided by the external payment provider upon successful completion.
 * @param error              Error message if the payment was not completed.
 */
@Schema(description = "Response data from the external payment service regarding a payment.")
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalPaymentResponse(
        @Schema(description = "The URI of payment page on the external payment service side for the payment completion.",
                example = "http://localhost:8083/views/payment/")
        @Nullable String paymentUri,

        @Schema(description = "The current state of the payment.", example = "PROCESSING")
        @Nonnull PaymentStatus status,

        @Schema(description = "Timestamp when the payment was successfully completed by the provider.",
                example = "2025-10-15T14:31")
        @Nullable LocalDateTime completionDateTime,

        @Schema(description = "Error message if the payment could not be completed.", example = "Payment completion impossible.")
        @Nullable String error
) {
}
