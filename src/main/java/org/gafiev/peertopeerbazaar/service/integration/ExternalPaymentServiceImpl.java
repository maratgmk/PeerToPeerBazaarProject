package org.gafiev.peertopeerbazaar.service.integration;

import lombok.AllArgsConstructor;
import org.gafiev.peertopeerbazaar.dto.integreation.request.ExternalPaymentRequest;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.service.integration.interfaces.ExternalPaymentService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@AllArgsConstructor
public class ExternalPaymentServiceImpl implements ExternalPaymentService {
    private final RestClient externalPaymentServiceClient;

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
