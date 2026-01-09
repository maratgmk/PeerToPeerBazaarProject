package org.gafiev.peertopeerbazaar.service.integration;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalPaymentRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalPaymentService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Service implementation for interacting with the external payment service.
 * This class handles communication with the external payment provider via REST API calls.
 */
@Service
@AllArgsConstructor
public class ExternalPaymentServiceImpl implements ExternalPaymentService {
    private final RestClient externalPaymentServiceClient;

    /**
     * Creates a new payment transaction by sending a request to the external payment service.
     * This method posts the payment request to the "/create" endpoint and retrieves the response.
     *
     * @param externalPaymentRequest the request object containing payment details
     * @return the response from the external payment service, including payment status and URI for the payment page
     */
    @Override
    public ExternalPaymentResponse createTransaction(ExternalPaymentRequest externalPaymentRequest) {
        return externalPaymentServiceClient.post()
                .uri("/create")
                .body(externalPaymentRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ExternalPaymentResponse.class);
    }
}
