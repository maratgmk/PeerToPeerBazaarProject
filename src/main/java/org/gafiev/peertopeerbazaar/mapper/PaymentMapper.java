package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalPaymentRequest;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.properties.PaymentProperties;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PaymentMapper {
    private final PaymentProperties paymentProperties;

    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMode(payment.getPaymentMode())
                .paymentStatus(payment.getPaymentStatus())
                .completionDateTime(payment.getCompletionDateTime())
                .build();
    }

    public Set<PaymentResponse> toPaymentResponseSet(Set<Payment> paymentSet) {
        return paymentSet == null ? null : paymentSet.stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toSet());
    }

    public ExternalPaymentRequest toExternalPaymentRequest(Payment payment) {
        return ExternalPaymentRequest.builder()
                .paymentId(payment.getId())
                .currency(payment.getCurrency())
                .amount(payment.getAmount())
                .callbackUri(paymentProperties.getCallbackUri().toString())
                .returnUri(paymentProperties.getReturnUri().toString())
                .merchantId(paymentProperties.getMerchantId())
                .signature(paymentProperties.getSecretKey())
                .build();
    }

}
//TODO   signature добавить шифрование