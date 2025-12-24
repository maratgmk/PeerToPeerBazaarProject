package org.gafiev.peertopeerbazaar.dto.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import lombok.Builder;

/**
 * DTO representing the response sent to the buyer to complete the payment transaction.
 * This record contains the payment ID and the URI of the payment page where the buyer
 * can enter card details and finalize the transaction.
 *
 * @param id Unique identifier of the payment record.
 * @param paymentPageUri URI of the payment page provided by the external payment service.
 */
@Schema(description = "Response data sent to the buyer to complete the payment transaction.")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PaymentRedirectResponse(
        @Schema(description = "Unique identifier of the payment record.", example = "27")
        @Nonnull Long id,

        @Schema(description = "URI of the payment page on the external payment service side for completing the payment.",
                example = "http://localhost:8083/payment/87")
        @Nonnull String paymentPageUri
) {
}