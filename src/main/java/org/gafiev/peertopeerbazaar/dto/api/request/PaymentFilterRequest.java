package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
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

/**
 * Data Transfer Object containing criteria for filtering payment records.
 * All parameters are optional and act as filters when provided.
 *
 * @param ids                      A set of specific payment IDs to filter by.
 * @param amountLow                The minimum transaction amount (inclusive).
 * @param amountHigh               The maximum transaction amount (inclusive).
 * @param paymentMode              Filter by the specific payment method used.
 * @param paymentStatus            The specific status of the payment as stored in the database.
 * @param completionDateTimeAfter  The start of the completion date range (inclusive).
 * @param completionDateTimeBefore The end of the completion date range (inclusive).
 */
@Schema(description = "Request criteria for filtering payment transactions.")
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record PaymentFilterRequest(

        @Schema(description = "Set of payment IDs to include in the search.", example = "[17,19,27]")
        @Size(min = 1) Set<Long> ids,

        @Schema(description = "Minimum transaction amount filter.", example = "525.59")
        @Positive
        @Digits(integer = 8, fraction = 2) BigDecimal amountLow,

        @Schema(description = "Maximum transaction amount filter.", example = "999.99")
        @Positive
        @Digits(integer = 8, fraction = 2) BigDecimal amountHigh,

        @Schema(description = "Filter by payment method.", example = "PAY_PAL")
        PaymentMode paymentMode,

        @Schema(description = "Filter by the payment status stored in the system.", example = "DENIED")
        PaymentStatus paymentStatus,

        @Schema(description = "Start of the completion date-time range.", example = "2025-10-15T10:31")
        @PastOrPresent
        LocalDateTime completionDateTimeAfter,

        @Schema(description = "End of the completion date-time range.", example = "2025-11-15T18:01")
        @PastOrPresent
        LocalDateTime completionDateTimeBefore
) {
}
