package org.gafiev.peertopeerbazaar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gafiev.peertopeerbazaar.dto.api.request.PaymentUpdateRequest;
import org.gafiev.peertopeerbazaar.dto.api.response.PaymentResponse;
import org.gafiev.peertopeerbazaar.dto.integreation.response.ExternalPaymentResponse;
import org.gafiev.peertopeerbazaar.exception.EntityNotFoundException;
import org.gafiev.peertopeerbazaar.service.model.interfaces.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment Callback", description = "Endpoints for receiving asynchronous payment status updates from external providers.")
@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "callback")
public class ExternalPaymentController {
    private final PaymentService paymentService;

    @Operation(summary = "Handle payment provider notification",
            description = "Receives asynchronous payment status updates (webhooks) from the external payment gateway.")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Callback processed successfully"),
            @ApiResponse(responseCode = "404", description = "Payment record not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error during processing")

    })

    @PostMapping(path = "/notify/{paymentId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> notify(@NotNull @PathVariable Long paymentId,
                                         @Parameter(description = "External payment status data", required = true)
                                         @RequestBody ExternalPaymentResponse paymentResponse) {

        log.info("Processing async payment notification [ID: {}, status: {}]", paymentId, paymentResponse.status());

        try {
            PaymentResponse response = paymentService.updatePayment(paymentId, PaymentUpdateRequest.builder()
                    .paymentStatus(paymentResponse.status())
                    .completionDateTime(paymentResponse.completionDateTime())
                    .build());

            log.info("Payment synchronization completed: {}", response);

            return ResponseEntity.ok("Callback accepted");

        } catch (EntityNotFoundException e) {
            log.warn("Payment record not found for ID {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment not found");

        } catch (Exception e) {
            log.error("Critical error processing payment callback for ID {}: {}", paymentId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing notification");
        }
    }
}
