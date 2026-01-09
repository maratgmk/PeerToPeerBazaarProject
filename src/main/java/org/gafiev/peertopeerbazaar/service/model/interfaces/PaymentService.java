package org.gafiev.peertopeerbazaar.service.model.interfaces;

import org.gafiev.peertopeerbazaar.dto.api.request.PaymentFilterRequest;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentRedirectResponse;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;

import java.util.Set;

/**
 * Provides methods for Payment data operations.
 */
public interface PaymentService {

    /**
     * Retrieves payment by ID.
     *
     * @param id Unique payment identifier (ID).
     * @return PaymentResponse DTO.
     */
    PaymentResponse getPaymentById(Long id);

    /**
     * Retrieves payment by ID, eager fetching the associated buyer orders.
     *
     * @param id Unique payment identifier (ID).
     * @return PaymentResponse DTO.
     */
    PaymentResponse getPaymentByIdWithBuyerOrders(Long id);

    /**
     * Retrieves Set of Payment responses based on filter criteria.
     *
     * @param filterRequest PaymentFilterRequest DTO containing filter criteria data.
     * @return Set of PaymentResponse DTOs.
     */
    Set<PaymentResponse> getAllPaymentSet(PaymentFilterRequest filterRequest);

    /**
     * Updates an existing payment using new data from the PaymentUpdateRequest DTO.
     *
     * @param id Unique payment identifier (ID).
     * @param paymentNew PaymentUpdateRequest DTO containing new data for payment updating.
     * @return PaymentResponse DTO.
     */
    PaymentResponse updatePayment(Long id, PaymentUpdateRequest paymentNew);

    /**
     * Finalizes the payment process and provides a redirect URI to the external payment page.
     * The response contains the URL where the buyer can complete the transaction.
     *
     * @param id Unique payment identifier (ID).
     * @return PaymentRedirectResponse DTO containing the checkout page URI.
     */
    PaymentRedirectResponse completePayment(Long id);

    /**
     * Deletes a payment by its ID from database.
     *
     * @param id Unique payment identifier (ID).
     */
    void deletePayment(Long id);
}
