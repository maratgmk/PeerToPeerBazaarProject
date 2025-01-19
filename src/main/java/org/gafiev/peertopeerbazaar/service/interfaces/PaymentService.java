package org.gafiev.peertopeerbazaar.service.interfaces;

import org.gafiev.peertopeerbazaar.dto.request.PaymentCreateRequest;
import org.gafiev.peertopeerbazaar.dto.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.response.PaymentResponse;

import java.util.Set;

/**
 * интерфейс объявляет методы работы с DTO платежами,
 * создание и обновление платежей, удаление платежа,
 * нахождение DTO платежа по его Id, нахождение множества DTO всех платежей,
 * и выборка подмножеств из этого множества согласно фильтра
 */
public interface PaymentService {

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByIdWithBuyerOrder(Long id);

    Set<PaymentResponse> getAllPaymentSet(PaymentFilterRequest filterRequest);

    PaymentResponse createPayment(PaymentCreateRequest paymentCreate);

    PaymentResponse updatePayment(Long id, PaymentCreateRequest paymentNew);

    void deletePayment(Long id);
}
