package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * The PaymentResponse DTO represents a complete payment structure.
 * Used as the response body in payment management API endpoints.
 *
 * @param id Unique identifier of the payment record.
 * @param amount The total monetary value of the transaction.
 * @param paymentMode The method used to process the payment (e.g., Credit Card, Crypto).
 * @param currency The currency code used for the transaction (ISO 4217).
 * @param paymentStatus  The current lifecycle state of the payment.
 * @param completionDateTime The timestamp provided by the external payment provider upon successful processing.
 * @param buyerOrderResponseSet A collection of orders associated with this payment.
 */
@Schema(description = "Represents detailed information about a payment transaction.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PaymentResponse(
        @Schema(description = "Unique payment identifier",example = "29")
        Long id,
        @Schema(description = "Total transaction amount", example = "5758.52")
        BigDecimal amount,
        @Schema(description = "The selected payment method", example = "CRYPTO_CURRENCY")
        PaymentMode paymentMode,
        @Schema(description = "Currency of the transaction", example = "BTC")
        CurrencyCode currency,
        @Schema(description = "Current status of the payment", example = "PROCESSING")
        PaymentStatus paymentStatus,
        @Schema(description = "Timestamp when the payment was finalized by the provider", example = "2025-10-15T14:31")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime completionDateTime,
        @Schema(description = "Set of associated buyer orders")
        Set<BuyerOrderResponse> buyerOrderResponseSet ){
}
