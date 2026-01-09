package org.gafiev.peertopeerbazaar.dto.integreation.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.neovisionaries.i18n.CurrencyCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.NonNull;

import java.math.BigDecimal;

/**
 * Data structure representing a request sent to the external payment service to initiate transaction completion.
 *
 * @param paymentId   The unique identifier for the payment within this application.
 * @param amount      The total monetary value of the transaction.
 * @param currency    The ISO 4217 currency code for the transaction.
 * @param callbackUri The webhook URI where the external payment service sends server-to-server notifications
 *                    regarding the payment result (e.g., SUCCESS or DENIED).
 * @param returnUri   The browser-redirect URI where the user is sent after completing the transaction
 *                    on the external payment page.
 * @param merchantId  The unique merchant account identifier assigned by the payment provider.
 * @param signature   A security hash for request validation (SHA-256 encrypted concatenation
 *                    of alphabetically sorted fields and the secret key).
 */
@Schema(description = "The request structure used to initiate a transaction with the external payment gateway.")
@Builder(toBuilder = true)
public record ExternalPaymentRequest(
        @Schema(description = "Unique internal payment ID", example = "87")
        @Nonnull @NonNull Long paymentId,

        @Schema(description = "Total transaction amount", example = "2789.31")
        @Nonnull @PositiveOrZero(message = "Price can not be negative")
        @Digits(integer = 12, fraction = 2)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "%.2f")
        BigDecimal amount,

        @Schema(description = "Currency code (ISO 4217)", example = "BTC")
        @Nonnull @NonNull CurrencyCode currency,

        @Schema(description = "Server-to-server notification URL (webhook)", example = "http://localhost:8080/callback/notify/")
        @Nonnull @NonNull String callbackUri,

        @Schema(description = "Client redirect URL after checkout", example = "http://localhost:8080/frontend/return/")
        @Nonnull @NonNull String returnUri,

        @Schema(description = "Merchant account identifier provided by the payment gateway", example = "testmerchantId")
        @Nonnull @NonNull String merchantId,

        @Schema(description = "HMAC/SHA-256 signature for request integrity", example = "testsecretKey + a5e8f...signature")
        @Nonnull @NonNull String signature
) {
}
