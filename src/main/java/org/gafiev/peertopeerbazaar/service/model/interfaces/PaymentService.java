package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentRedirectResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;

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

    PaymentResponse updatePayment(Long id, PaymentUpdateRequest paymentNew);

    PaymentRedirectResponse completePayment(Long id);

    void deletePayment(Long id);
}
