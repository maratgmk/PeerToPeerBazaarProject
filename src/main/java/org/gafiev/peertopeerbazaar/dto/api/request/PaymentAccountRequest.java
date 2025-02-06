package org.gafiev.peertopeerbazaar.dto.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.neovisionaries.i18n.CurrencyCode;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentAccountRequest(

        @NotBlank
        @Pattern(regexp = "\\d{1,20}", message = "Account number must be numerical")
        String accountNumber,

        @Nonnull
        CurrencyCode currency,

        @Nonnull @Digits(integer = 7, fraction = 2)
        BigDecimal balance,

        @Nonnull
        PaymentMode paymentMode,

        @Nonnull
        PaymentStatus paymentStatus,

        @NotBlank @Pattern(regexp = "^[A-Z0-9]{8,11}$", message = "Bank code must be alphanumerical and has from 8 to 11 symbols")
        String bankCode,

        @Nonnull @Digits(integer = 4, fraction = 2)
        BigDecimal accountLimit,

        @Nonnull @AssertFalse
        Boolean isVerified,

        @Nonnull
        String email,

        @Nonnull
        String password


) {
}
