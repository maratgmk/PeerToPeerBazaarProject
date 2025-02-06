package org.gafiev.peertopeerbazaar.service.model.interfaces;


import org.gafiev.peertopeerbazaar.dto.api.request.PaymentAccountFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentAccountRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentAccountResponse;

import java.util.Set;

/**
 * интерфейс объявляет CRUD и кастомные методы для работы с DTO paymentAccount,
 * поиск множества платёжных аккаунтов в БД по фильтру параметров и заданных условий
 */

public interface PaymentAccountService {

    PaymentAccountResponse getPaymentAccountById(Long Id);

    Set<PaymentAccountResponse> getAllPaymentAccounts(PaymentAccountFilterRequest filterRequest);

    PaymentAccountResponse createPaymentAccount(PaymentAccountRequest paymentCreateRequest);

    PaymentAccountResponse updatePaymentAccount(Long id, PaymentAccountRequest paymentAccountNew);

    void deletePaymentAccount(Long id);
}
