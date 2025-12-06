package org.gafiev.peertopeerbazaar.common;

import com.neovisionaries.i18n.CurrencyCode;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentMode;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentStatus;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataPayment {

    private static @NotNull BigDecimal getRandomBigDecimal() {
        return new BigDecimal(String.format(Locale.US, "%.2f", ThreadLocalRandom.current().nextDouble(0.0, 1000.0)));
    }

    public static Payment getPaymentCreated() {
        return Payment.builder()
                .paymentMode(PaymentMode.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.CREATED)
                .currency(CurrencyCode.RUB)
                .amount(getRandomBigDecimal())
                .completionDateTime(null)
                .createdAt(Instant.now().minusSeconds(600))
                .build();
    }

    public static Payment getPaymentProcessing() {
        return Payment.builder()
                .paymentMode(PaymentMode.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PROCESSING)
                .currency(CurrencyCode.RUB)
                .amount(getRandomBigDecimal())
                .completionDateTime(null)
                .createdAt(Instant.now().minusSeconds(600))
                .build();
    }

    public static Payment getPaymentSuccess() {
        return Payment.builder()
                .paymentMode(PaymentMode.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.SUCCESS)
                .currency(CurrencyCode.RUB)
                .amount(getRandomBigDecimal())
                .completionDateTime(LocalDateTime.from(Instant.now().minusSeconds(660)))
                .createdAt(Instant.now().minusSeconds(600))
                .build();
    }

    public static PaymentFilterRequest getPaymentFilterRequest(){
        return PaymentFilterRequest.builder()
                .ids(Set.of(11L,12L,13L))
                .amountHigh(BigDecimal.valueOf(500))
                .amountLow(BigDecimal.valueOf(90))
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentMode(PaymentMode.BANK_TRANSFER)
                .completionDateTimeAfter(LocalDateTime.of(2025,10,27, 9,00,00))
                .completionDateTimeBefore(LocalDateTime.of(2025,10,27, 15,00,00))
                .build();
    }

}
