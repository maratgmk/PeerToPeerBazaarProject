package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for updating an existing payment that has not yet been finalized.
 * Provides fields to modify transaction details and manage associated buyer orders.
 *
 * @param amount                The total monetary value of the transaction.
 * @param paymentMode           The method used to process the payment (e.g., Credit Card, Crypto).
 * @param paymentStatus         The current lifecycle state of the payment.
 * @param buyerOrderIdsToAdd    A set of buyer order IDs to be associated with this payment.
 * @param buyerOrderIdsToRemove A set of buyer order IDs to be disassociated from this payment.
 */
@Schema(description = "Request object for updating payment details and associated buyer order links.")
@Builder(toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentUpdateRequest(

        @Schema(description = "Total transaction amount", example = "10378.27")
        @Nullable @Positive
        @Digits(integer = 8, fraction = 2)
        BigDecimal amount,

        @Schema(description = "The selected payment method", example = "BARTER")
        @Nullable
        PaymentMode paymentMode,

        @Schema(description = "Current status of the payment", example = "CREATED")
        @Nullable
        PaymentStatus paymentStatus,

        @Schema(description = "Timestamp when the payment was finalized by the provider", example = "2025-10-15T14:31")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime completionDateTime,

        @Schema(description = "Set of buyer order IDs to add to the payment.", example = "[2,6,9]")
        @Nullable
        Set<@Positive Long> buyerOrderIdsToAdd,

        @Schema(description = "Set of buyer order IDs to remove to the payment.", example = "[3,5,7]")
        @Nullable
        Set<@Positive Long> buyerOrderIdsToRemove) {
}
