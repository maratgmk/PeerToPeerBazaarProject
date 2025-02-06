package org.gafiev.peertopeerbazaar.mapper;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentAccountResponse;
import org.gafiev.peertopeerbazaar.entity.payment.PaymentAccount;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PaymentAccountMapper {
    public PaymentAccountResponse toPaymentAccountResponse(PaymentAccount account) {
        return PaymentAccountResponse.builder()
                .id(account.getId())
                .currency(account.getCurrency())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .accountLimit(account.getAccountLimit())
                .paymentStatus(account.getPaymentStatus())
                .bankCode(account.getBankCode())
                .paymentMode(account.getPaymentMode())
                .createdDate(account.getCreatedAccountDate())
                .updatedDate(account.getUpdatedAccountDate())
                .isVerified(account.getIsVerified())
                .userId(account.getUser() == null ? null : account.getUser().getId()) //если нет user, то нет и paymentAccount?
                .build();
    }

    public Set<PaymentAccountResponse> toPaymentAccountResponseSet(Set<PaymentAccount> accounts) {
        return accounts == null ? null : accounts.stream()
                .map(this::toPaymentAccountResponse)
                .collect(Collectors.toSet());
    }
}
