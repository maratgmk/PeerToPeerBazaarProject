package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalPaymentRequest;
import org.gafiev.peertopeerbazaar.entity.payment.Payment;
import org.gafiev.peertopeerbazaar.properties.PaymentProperties;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper class for converting Payment entities to various DTOs (Data Transfer Objects).
 */
@Component
@AllArgsConstructor
public class PaymentMapper {
    private final PaymentProperties paymentProperties;
    private final BuyerOrderMapper buyerOrderMapper;

    /**
     * Converts Payment entity to PaymentResponse DTO.
     *
     * @param payment Payment entity.
     * @return PaymentResponse DTO.
     */
    public PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMode(payment.getPaymentMode())
                .paymentStatus(payment.getPaymentStatus())
                .completionDateTime(payment.getCompletionDateTime())
                .buyerOrderResponseSet(buyerOrderMapper.toBuyerOrderResponseSet(payment.getBuyerOrderSet()))
                .build();
    }

    /**
     * Converts Set of Payment entities to Set of PaymentResponse DTOs.
     *
     * @param paymentSet Set of Payment entities.
     * @return Set of PaymentResponse DTOs.
     */
    public Set<PaymentResponse> toPaymentResponseSet(Set<Payment> paymentSet) {
        return paymentSet == null ? null : paymentSet.stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toSet());
    }

    /**
     * Converts Payment entity to ExternalPaymentRequest DTO.
     *
     * @param payment Payment entity.
     * @return ExternalPaymentRequest DTO.
     */
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