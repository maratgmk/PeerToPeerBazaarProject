package org.gafiev.peertopeerbazaar.service.interfaces;


import org.gafiev.peertopeerbazaar.dto.request.PaymentAccountFilterRequest;
import org.gafiev.peertopeerbazaar.dto.request.PaymentAccountRequest;
import org.gafiev.peertopeerbazaar.dto.request.PaymentCreateRequest;
import org.gafiev.peertopeerbazaar.dto.response.PaymentAccountResponse;

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
