package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PaymentMapper {

    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
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

}
