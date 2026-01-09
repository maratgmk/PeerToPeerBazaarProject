package org.gafiev.peertopeerbazaar.service.integration.interfaces;

import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalPaymentRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;

/**
 * Service interface for interacting with the external payment service.
 * This interface defines the contract for handling payment transactions,
 * including creating payments and processing callbacks from the external provider.
 */
public interface ExternalPaymentService {

    /**
     * Creates a new payment transaction by sending a request to the external payment service.
     * The request includes details such as amount, currency, payment ID, merchant ID, callback URI,
     * return URI, and a security signature. In response, the service provides a URI to the payment page
     * where the user can enter card details. After payment, the user is redirected back to our application,
     * and a callback is sent to notify about the payment status.
     *
     * @param externalPaymentRequest The DTO request object containing payment details (e.g., amount, currency, IDs, URIs, signature).
     * @return The response DTO from the external payment service, including the payment status and URI for the payment page.
     */
    ExternalPaymentResponse createTransaction(ExternalPaymentRequest externalPaymentRequest);
}

